import {
  Component,
  OnInit,
  AfterViewInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.html'
})
export class Analytics implements OnInit, AfterViewInit {

  analytics: any = null;
  kpis: any = null;

  dailyChart: any;
  statusChart: any;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  ngAfterViewInit(): void {}

  loadAnalytics(): void {
    this.http.get<any>(API.ADMIN.SNOWFLAKE)
      .subscribe({
        next: (data) => {
          this.analytics = data;
          this.kpis = data.kpis;
          this.cdr.detectChanges();
          setTimeout(() => this.renderCharts(), 0);
        },
        error: (err) => {
          console.error('Analytics load failed', err);
        }
      });
  }

  calculateGrowthRate(monthlyTrend: any[]): string {
    if (!monthlyTrend || monthlyTrend.length < 2) return '0';

    const last = monthlyTrend[monthlyTrend.length - 1].TOTAL_AMOUNT;
    const prev = monthlyTrend[monthlyTrend.length - 2].TOTAL_AMOUNT;

    if (!prev) return '0';

    return (((last - prev) / prev) * 100).toFixed(2);
  }

  renderCharts(): void {
    if (!this.analytics) return;

    this.renderDailyChart(this.analytics.dailyTrend);
    this.renderStatusChart(this.analytics.statusBreakdown);
  }

  renderDailyChart(data: any[]): void {

    if (this.dailyChart) this.dailyChart.destroy();

    const labels = data.map(d => d.FULL_DATE);
    const values = data.map(d => d.TOTAL_AMOUNT);

    this.dailyChart = new Chart('dailyChart', {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Daily Amount',
          data: values,
          borderWidth: 2,
          tension: 0.3
        }]
      }
    });
  }

  renderStatusChart(data: any[]): void {

    if (this.statusChart) this.statusChart.destroy();

    const labels = data.map(s => s.STATUS);
    const values = data.map(s => s.COUNT);

    this.statusChart = new Chart('statusChart', {
      type: 'pie',
      data: {
        labels,
        datasets: [{
          data: values
        }]
      }
    });
  }
}