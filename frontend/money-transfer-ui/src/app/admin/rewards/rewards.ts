import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { API } from '../../core/api';
import { PaginationComponent } from '../../shared/pagination/pagination';

@Component({
  selector: 'app-admin-rewards',
  standalone: true,
  imports: [CommonModule, RouterModule, PaginationComponent],
  templateUrl: './rewards.html'
})
export class AdminRewards implements OnInit {

  summary: any = { totalPointsIssued: 0, totalPointsRedeemed: 0, totalPointsOutstanding: 0 };
  accountSummaries: any[] = [];
  redemptions: any[] = [];

  activeTab: 'accounts' | 'redemptions' = 'accounts';

  pageSize = 10;
  currentPage = 1;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadSummary();
    this.loadAccountSummaries();
    this.loadRedemptions();
  }

  loadSummary(): void {
    this.http.get<any>(API.ADMIN.REWARDS_SUMMARY)
      .subscribe({
        next: (data) => { this.summary = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Failed to load reward summary:', err)
      });
  }

  loadAccountSummaries(): void {
    this.http.get<any[]>(API.ADMIN.REWARDS_ACCOUNTS)
      .subscribe({
        next: (data) => { this.accountSummaries = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Failed to load account reward summaries:', err)
      });
  }

  loadRedemptions(): void {
    this.http.get<any[]>(API.ADMIN.REWARDS_REDEMPTIONS)
      .subscribe({
        next: (data) => { this.redemptions = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Failed to load redemptions:', err)
      });
  }

  setTab(tab: 'accounts' | 'redemptions'): void {
    this.activeTab = tab;
    this.currentPage = 1;
  }

  get paginatedAccounts() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.accountSummaries.slice(start, start + this.pageSize);
  }

  get paginatedRedemptions() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.redemptions.slice(start, start + this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}