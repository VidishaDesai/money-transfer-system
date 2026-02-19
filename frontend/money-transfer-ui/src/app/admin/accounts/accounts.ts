import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { API } from '../../core/api';
import { PaginationComponent } from '../../shared/pagination/pagination';

@Component({
  selector: 'app-admin-accounts',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, PaginationComponent],
  templateUrl: './accounts.html'
})
export class Accounts implements OnInit {

  accounts: any[] = [];
  filteredAccounts: any[] = [];

  filters = {
    status: '',
    approved: ''
  };

  pageSize = 10;
  currentPage = 1;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.http.get<any[]>(API.ADMIN.ALL_ACCOUNTS)
      .subscribe({
        next: (data) => {
          this.accounts = data.slice(1); // skip admin account
          this.applyFilters();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to load accounts:', err);
        }
      });
  }

  applyFilters(): void {

    this.filteredAccounts = this.accounts.filter(acc => {

      if (this.filters.status &&
          acc.status !== this.filters.status) {
        return false;
      }

      if (this.filters.approved !== '') {
        const approvedBool = this.filters.approved === 'true';
        if (acc.approved !== approvedBool) {
          return false;
        }
      }

      return true;
    });

    this.currentPage = 1;
  }

  formatAmount(amount: number): string {
    return Number(amount).toFixed(2);
  }

  get paginatedAccounts() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredAccounts.slice(start, start + this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }
}