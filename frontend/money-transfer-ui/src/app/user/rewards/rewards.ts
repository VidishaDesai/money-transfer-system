import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  loading = false;
  error = '';

  pageSize = 10;
  currentPage = 1;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRewards();
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

  get paginatedRewards() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.rewards.slice(start, start + this.pageSize);
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString();
  }
}
