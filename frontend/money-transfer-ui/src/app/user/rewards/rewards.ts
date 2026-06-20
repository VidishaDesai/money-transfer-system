import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import { PaginationComponent } from '../../shared/pagination/pagination';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-rewards',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent, RouterModule],
  templateUrl: './rewards.html',
  styleUrl: './rewards.css'
})
export class Rewards implements OnInit {

  rewards: any[] = [];
  redemptions: any[] = [];
  balance = 0;

  loading = false;
  error = '';

  activeTab: 'earned' | 'redeemed' = 'earned';

  pageSize = 10;
  currentPage = 1;

  // Redeem form
  redeemPoints = 0;
  redeemDescription = '';
  redeeming = false;
  message = '';
  messageType = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRewards();
    this.loadBalance();
    this.loadRedemptions();
  }

  loadRewards(): void {
    this.loading = true;
    this.http.get<any[]>(API.REWARDS.HISTORY)
      .subscribe({
        next: (data) => {
          this.rewards = data;
          this.error = '';
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to load reward history:', err);
          this.error = 'Unable to load reward history.';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  loadBalance(): void {
    this.http.get<any>(API.REWARDS.BALANCE)
      .subscribe({
        next: (data) => {
          this.balance = data.balance;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Failed to load reward balance:', err)
      });
  }

  loadRedemptions(): void {
    this.http.get<any[]>(API.REWARDS.REDEMPTIONS)
      .subscribe({
        next: (data) => {
          this.redemptions = data;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Failed to load redemption history:', err)
      });
  }

  redeem(form: NgForm): void {
    if (this.redeeming) return;

    this.message = '';

    if (!this.redeemPoints || this.redeemPoints <= 0) {
      this.message = 'Enter a valid number of points to redeem.';
      this.messageType = 'error';
      return;
    }

    if (this.redeemPoints > this.balance) {
      this.message = 'You do not have enough reward points.';
      this.messageType = 'error';
      return;
    }

    this.redeeming = true;

    this.http.post(API.REWARDS.REDEEM, {
      points: this.redeemPoints,
      description: this.redeemDescription
    }).subscribe({
      next: () => {
        this.message = 'Points redeemed successfully.';
        this.messageType = 'success';
        this.redeemPoints = 0;
        this.redeemDescription = '';
        form.resetForm();
        this.redeeming = false;
        this.loadBalance();
        this.loadRedemptions();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.message = err?.error?.message || 'Redemption failed.';
        this.messageType = 'error';
        this.redeeming = false;
        this.cdr.detectChanges();
      }
    });
  }

  setTab(tab: 'earned' | 'redeemed'): void {
    this.activeTab = tab;
    this.currentPage = 1;
  }

  get paginatedRewards() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.rewards.slice(start, start + this.pageSize);
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