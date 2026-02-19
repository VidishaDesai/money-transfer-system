import { Component, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef } from '@angular/core';
import { API } from '../../core/api';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile {

  profile: any = null;
  showMenu = false;
  accountId = localStorage.getItem('accountId');

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private cdr: ChangeDetectorRef,
    private elementRef: ElementRef
  ) {}

  toggleMenu() {
    this.showMenu = !this.showMenu;

    if (this.showMenu && !this.profile) {
      this.loadProfile();
    }
  }

  loadProfile() {
    this.http.get(API.ACCOUNTS.DETAILS(Number(this.accountId)))
      .subscribe({
        next: (data) => {
          this.profile = data;
          this.cdr.detectChanges();
        },
        error: () => console.error('Failed to load profile')
      });
  }

  // Close when clicking outside
  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showMenu = false;
    }
  }
}