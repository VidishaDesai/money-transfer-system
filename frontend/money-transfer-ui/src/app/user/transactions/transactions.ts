import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import { RouterModule } from '@angular/router';
import { PaginationComponent } from '../../shared/pagination/pagination';
import { FormsModule } from '@angular/forms';
import { ConfirmDialog } from '../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    FormsModule,
    PaginationComponent,
    ConfirmDialog
  ],
  templateUrl: './transactions.html'
})
export class Transactions implements OnInit {

  transactions: any[] = [];
  filteredTransactions: any[] = [];

  filters = {
    transactionType: '',
    status: '',
    dateRange: ''
  };

  pageSize = 10;
  currentPage = 1;

  // rollback dialog state
  showRollbackDialog = false;
  pendingRollbackId: string | null = null;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.reloadTransactions();
  }

  reloadTransactions(): void {
    this.http.get<any[]>(API.TRANSFERS.HISTORY)
      .subscribe({
        next: (data) => {
          this.transactions = data;
          this.applyFilters();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to load transactions:', err);
        }
      });
  }

  applyFilters(): void {

    const now = new Date();

    this.filteredTransactions = this.transactions.filter(tx => {

      if (this.filters.transactionType &&
          tx.transactionType !== this.filters.transactionType) {
        return false;
      }

      if (this.filters.status &&
          tx.status !== this.filters.status) {
        return false;
      }

      if (this.filters.dateRange) {

        const txDate = new Date(tx.createdOn);

        const todayStart = new Date(now);
        todayStart.setHours(0,0,0,0);

        const yesterdayStart = new Date(todayStart);
        yesterdayStart.setDate(todayStart.getDate() - 1);

        const weekStart = new Date(todayStart);
        weekStart.setDate(todayStart.getDate() - 7);

        const monthStart = new Date(todayStart);
        monthStart.setDate(todayStart.getDate() - 30);

        switch (this.filters.dateRange) {

          case 'today':
            if (txDate < todayStart) return false;
            break;

          case 'yesterday':
            if (txDate < yesterdayStart || txDate >= todayStart) return false;
            break;

          case 'week':
            if (txDate < weekStart) return false;
            break;

          case 'month':
            if (txDate < monthStart) return false;
            break;
        }
      }

      return true;
    });

    this.currentPage = 1;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }

  getOtherAccount(tx: any): string {
    return tx.transactionType === 'DEBIT'
      ? tx.toAccountId
      : tx.fromAccountId;
  }

  // open dialog
  requestRollback(transactionId: string): void {
    this.pendingRollbackId = transactionId;
    this.showRollbackDialog = true;
  }

  // confirm dialog
  confirmRollback(): void {

    if (!this.pendingRollbackId) return;

    const rollbackId = this.pendingRollbackId;

    this.showRollbackDialog = false;
    this.pendingRollbackId = null;
    this.cdr.detectChanges();

    this.http.post(API.TRANSFERS.ROLLBACK(rollbackId), {}, { responseType: 'text' })
      .subscribe({
        next: () => {

          // Instant UI update (optimistic update)
          this.transactions = this.transactions.map(tx =>
            tx.id === rollbackId
              ? { ...tx, status: 'ROLLBACK_REQUESTED' }
              : tx
          );
          console.log('Updated transactions:', this.transactions);

          this.applyFilters();
          this.cdr.detectChanges();

          // Small delay ensures UI renders before reload
          setTimeout(() => {
            this.reloadTransactions();
          }, 150);
        },

        error: (err) => {
          console.error(err);
          this.cdr.detectChanges();
        }
      });
  }

  // cancel dialog
  cancelRollback(): void {
    this.showRollbackDialog = false;
    this.pendingRollbackId = null;
  }

  get paginatedTransactions() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredTransactions.slice(start, start + this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }
}