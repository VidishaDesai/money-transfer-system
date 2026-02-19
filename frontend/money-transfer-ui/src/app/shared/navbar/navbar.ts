import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ModalService } from '../../core/services/modal.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

  darkMode = false;
  showLogoutModal = false;

  constructor(
    public auth: AuthService,
    private modal: ModalService,
    private router: Router
  ) {
    // restore preference
    this.darkMode = localStorage.getItem('darkMode') === 'true';
    this.applyTheme();
  }

  isHomePage(): boolean {
    return this.router.url === '/';
  }


  logout(): void {
    this.auth.logout();
  }

  toggleDarkMode(): void {
    this.darkMode = !this.darkMode;
    localStorage.setItem('darkMode', String(this.darkMode));
    this.applyTheme();
  }

  private applyTheme(): void {
    if (this.darkMode) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }

  getDashboardRoute(): string {
    const role = localStorage.getItem('role');
    return role === 'ROLE_ADMIN' ? '/admin' : '/dashboard';
  }

  openLogoutModal() {
    this.modal.openLogout();
  }

  closeLogoutModal() {
    this.showLogoutModal = false;
  }

  confirmLogout() {
    this.showLogoutModal = false;
    this.auth.logout();
  }
}