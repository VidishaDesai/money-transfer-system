import { Component, ChangeDetectorRef, ElementRef, ViewChild, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { API } from '../../core/api';
import { FormService } from '../../core/services/form.service';
import { DatePicker } from '../../shared/date-picker/date-picker';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule, DatePicker],
  templateUrl: './register.html'
})
export class Register {

  formData = {
    holderName: '',
    email: '',
    password: '',
    phone: '',
    address: '',
    dateOfBirth: '',
    accountType: ''
  };

  confirmPassword = '';

  accountTypes = ['SAVINGS','STUDENT','CURRENT','SALARY','NRE','NRO','BUSINESS','PREMIUM','JOINT','SENIOR'];

  message = '';
  messageType = '';
  loading = false;

  showCalendar = false;

  @ViewChild('datePickerWrapper') datePickerWrapper!: ElementRef;

  onDateSelected(date: string) {
    this.formData.dateOfBirth = date;
    this.showCalendar = false;
  }

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private form: FormService
  ) {}

  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {

    if (!this.datePickerWrapper) return;

    const clickedInside =
      this.datePickerWrapper.nativeElement.contains(event.target);

    if (!clickedInside) {
      this.showCalendar = false;
    }
  }


  onRegister(form: any): void {

    if (this.loading) return;

    if (this.formData.password !== this.confirmPassword) {
      this.form.setError(this, null, 'Passwords do not match');
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;

    this.http.post(API.AUTH.REGISTER, this.formData)
      .subscribe({
        next: () => {

          this.form.setSuccess(
            this,
            'Registration successful! Your account is pending admin approval. You will be redirected shortly.'
          );

          form.resetForm();
          this.confirmPassword = '';
          this.showCalendar = false;

          this.cdr.detectChanges();

          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },

        error: (err) => {
          this.form.setError(this, err, 'Registration failed');
          this.cdr.detectChanges();
        }
      });
  }
}