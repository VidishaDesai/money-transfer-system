import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BalanceService {

  private balanceChangedSource = new Subject<void>();
  balanceChanged$ = this.balanceChangedSource.asObservable();

  notifyBalanceChanged(): void {
    this.balanceChangedSource.next();
  }
}