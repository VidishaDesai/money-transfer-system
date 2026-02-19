import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import { RouterModule } from '@angular/router';
import { FormService } from '../../core/services/form.service';
import { AccountSearch } from '../../shared/account-search/account-search';

@Component({
  selector: 'app-deposit',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    FormsModule,
    AccountSearch
  ],
  templateUrl: './deposit.html'
})
export class Deposit {

  accountsAPI = API.ADMIN.ALL_ACCOUNTS;

  accountId = 0;
  amount = 0;
  message = '';
  messageType = '';
  loading = false;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private form: FormService
  ) {}

  onAccountSelected(account: any): void {
    this.accountId = account.id;
  }

  deposit(form: NgForm): void {

    if (this.loading) return;

    this.loading = true;

    this.http.post(API.ADMIN.DEPOSIT, {
      accountId: this.accountId,
      amount: this.amount
    }).subscribe({
      next: () => {
        this.form.setSuccess(this, 'Deposit successful');
        this.accountId = 0;
        this.amount = 0;
        form.resetForm();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.form.setError(this, err, 'Deposit failed');
        this.cdr.detectChanges();
      }
    });
  }
}