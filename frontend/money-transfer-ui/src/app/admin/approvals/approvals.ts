import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import { RouterModule } from '@angular/router';
import { FormService } from '../../core/services/form.service';
import { PaginationComponent } from '../../shared/pagination/pagination';

@Component({
  selector: 'app-approvals',
  standalone: true,
  imports: [RouterModule, CommonModule, PaginationComponent],
  templateUrl: './approvals.html'
})
export class Approvals implements OnInit {

  accounts: any[] = [];
  loading = false;
  message = '';
  messageType = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private form: FormService
  ) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {

    this.loading = true;

    this.http.get<any[]>(API.ADMIN.PENDING)
      .subscribe({
        next: (data) => {
          this.accounts = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.form.setError(this, err, 'Failed to load pending accounts');
          this.cdr.detectChanges();
        }
      });
  }

  approve(id: number): void {

    if (this.loading) return;

    this.loading = true;

    this.http.post(API.ADMIN.APPROVE(id), {})
      .subscribe({
        next: () => {
          this.form.setSuccess(this, 'Account approved successfully');
          this.loadPending();   // reload list
        },
        error: (err) => {
          this.form.setError(this, err, 'Approval failed');
          this.cdr.detectChanges();
        }
      });
  }

  reject(id: number): void {

    if (this.loading) return;

    this.loading = true;

    this.http.post(API.ADMIN.REJECT(id), {})
      .subscribe({
        next: () => {
          this.form.setSuccess(this, 'Account rejected');
          this.loadPending();
        },
        error: (err) => {
          this.form.setError(this, err, 'Reject failed');
          this.cdr.detectChanges();
        }
      });
  }

  pageSize = 10;
  currentPage = 1;

  get paginatedAccounts() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.accounts.slice(start, start + this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }
}