import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.html'
})
export class AdminDashboard implements OnInit {

  holderName = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ROLE_ADMIN') {
      this.router.navigate(['/login']);
      return;
    }

    this.holderName = localStorage.getItem('holderName') || '';
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}