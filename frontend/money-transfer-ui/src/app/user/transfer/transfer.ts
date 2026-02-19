import {
  ChangeDetectorRef,
  Component
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { API } from '../../core/api';
import { RouterModule } from '@angular/router';
import { FormService } from '../../core/services/form.service';
import { BalanceService } from '../../core/services/balance.service';
import { AccountSearch } from '../../shared/account-search/account-search';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    FormsModule,
    AccountSearch
  ],
  templateUrl: './transfer.html'
})
export class Transfer {

  accountsAPI = API.ACCOUNTS.SEARCH;

  transferData = {
    fromAccountId: Number(localStorage.getItem('accountId')),
    toAccountId: 0,
    amount: 0,
    idempotencyKey: crypto.randomUUID()
  };

  currentBalance = Number(localStorage.getItem('balance') || 0);
  minBalance = 1000;

  loading = false;
  message = '';
  messageType = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private form: FormService,
    private balanceService: BalanceService
  ) {}

  onAccountSelected(acc: any) {
    this.transferData.toAccountId = acc?.id || 0;
  }

  transfer(): void {

    if (this.loading) return;

    if (!this.transferData.toAccountId) {
      this.form.setError(this, null, 'Please select a valid account');
      return;
    }

    this.loading = true;

    this.http.post(API.TRANSFERS.CREATE, this.transferData)
      .subscribe({
        next: () => {
          this.form.setSuccess(this, 'Transfer successful!');
          this.balanceService.notifyBalanceChanged();
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.form.setError(this, err, 'Transfer failed');
          this.cdr.detectChanges();
        }
      });
  }
}