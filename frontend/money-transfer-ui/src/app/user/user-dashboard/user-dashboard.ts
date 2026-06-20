import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { API } from '../../core/api';
import { AuthService } from '../../core/services/auth.service';
import { BalanceService } from '../../core/services/balance.service';
import { Profile } from '../profile/profile';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, Profile],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.css',
})
export class UserDashboard {

  profile: any = null;
  showProfileMenu = false;

  holderName = '';
  accountId = '';
  balance = '0.00';
  rewardPoints = 0;


  constructor(
    private http: HttpClient,
    private router: Router,
    private auth: AuthService,
    private cdr: ChangeDetectorRef,
    private balanceService: BalanceService
  ) {}

  ngOnInit(): void {

    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.holderName = localStorage.getItem('holderName') || '';
    this.accountId = localStorage.getItem('accountId') || '';

    this.loadBalance();

    this.balanceService.balanceChanged$.subscribe(() => {
      this.loadBalance();
    });
  }

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;

    if (this.showProfileMenu && !this.profile) {
      this.loadProfile();
    }
  }

  loadProfile(): void {
    this.http.get<any>(
      API.ACCOUNTS.DETAILS(Number(this.accountId))
    ).subscribe({
      next: (data) => {
        this.profile = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load profile', err);
      }
    });
  }


  loadBalance(): void {
  this.http.get<any>(
    API.ACCOUNTS.BALANCE(Number(this.accountId))
  ).subscribe({
      next: (data) => {
        this.balance = Number(data.balance).toFixed(2);
        this.rewardPoints = Number(data.rewardPoints ?? 0);
        console.log('Balance loaded:', this.balance, 'Reward points:', this.rewardPoints);
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.log(err);
      }
    });
  }


  logout() {
    this.auth.logout();
  }

}