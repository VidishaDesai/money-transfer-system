import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import { RouterModule } from '@angular/router';
import { PaginationComponent } from '../../shared/pagination/pagination';
import { FormService } from '../../core/services/form.service';
import { ConfirmDialog } from '../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-rollbacks',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    PaginationComponent,
    ConfirmDialog
  ],
  templateUrl: './rollbacks.html'
})
export class Rollbacks implements OnInit {

  rollbacks: any[] = [];

  loading = false;
  message = '';
  messageType = '';

  // modal
  showConfirm = false;
  confirmAction: 'approve' | 'reject' = 'approve';
  selectedTransactionId = '';

  pageSize = 10;
  currentPage = 1;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private form: FormService
  ) {}

  ngOnInit(): void {
    this.loadRollbacks();
  }

  loadRollbacks(): void {

    this.loading = true;

    this.http.get<any[]>(API.ADMIN.ROLLBACK_REQUESTS)
      .subscribe({
        next: (data) => {
          this.rollbacks = data || [];
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.form.setError(this, err, 'Failed to load rollback requests');
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  openConfirm(id: string, action: 'approve' | 'reject') {
    this.selectedTransactionId = id;
    this.confirmAction = action;
    this.showConfirm = true;
  }

  closeConfirm() {
    this.showConfirm = false;
  }

  confirmActionExecute(): void {

    if (this.loading) return;

    this.loading = true;

    const txId = this.selectedTransactionId;

    const api =
      this.confirmAction === 'approve'
        ? API.ADMIN.APPROVE_ROLLBACK(txId)
        : API.ADMIN.REJECT_ROLLBACK(txId);

    this.http.post(api, {}, { responseType: 'text' })
      .subscribe({
        next: () => {

          // SUCCESS MESSAGE
          this.form.setSuccess(
            this,
            `Rollback ${this.confirmAction}d successfully`
          );

          this.showConfirm = false;

          this.rollbacks = this.rollbacks.filter(
            r => r.id !== txId
          );

          // reset pagination if needed
          if (this.currentPage > 1 &&
              this.paginatedRollbacks.length === 0) {
            this.currentPage--;
          }

          this.loading = false;
          this.cdr.detectChanges();

          // Optional backend sync (safe refresh)
          setTimeout(() => {
            this.loadRollbacks();
          }, 200);
        },

        error: (err) => {
          this.form.setError(this, err, 'Action failed');
          this.loading = false;
          this.showConfirm = false;
          this.cdr.detectChanges();
        }
      });
  }

  formatDate(date: string): string {
    return date ? new Date(date).toLocaleString() : '';
  }

  get paginatedRollbacks() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.rollbacks.slice(start, start + this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }
}