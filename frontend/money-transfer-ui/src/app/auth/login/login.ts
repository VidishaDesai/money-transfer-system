import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { API } from '../../core/api';
import { FormService } from '../../core/services/form.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html'
})
export class Login {

  formData = {
    email: '',
    password: ''
  };

  message = '';
  messageType = '';
  loading = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private form: FormService
  ) {}

  onLogin(): void {

    if (this.loading) return;

    this.message = '';
    this.messageType = '';

    this.loading = true;

    this.http.post<any>(API.AUTH.LOGIN, this.formData)
      .subscribe({
        next: (data) => {

          console.log('Login successful:', data);

          // Store session data
          localStorage.setItem('token', data.token);
          localStorage.setItem('accountId', data.accountId);
          localStorage.setItem('holderName', data.holderName);
          localStorage.setItem('email', data.email);
          localStorage.setItem('role', data.role);
          localStorage.setItem('balance', data.balance);

          this.loading = false;

          // Navigate based on role
          if (data.role === 'ROLE_ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/dashboard']);
          }
        },

        error: (err) => {
          this.form.setError(
            this,
            err,
            'Login failed. Please check your credentials.'
          );
          this.cdr.detectChanges();
        }
      });
  }
}