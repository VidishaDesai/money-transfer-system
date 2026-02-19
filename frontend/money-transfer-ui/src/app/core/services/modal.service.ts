import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ModalService {

  private logoutModalSubject = new BehaviorSubject<boolean>(false);
  logoutModal$ = this.logoutModalSubject.asObservable();

  openLogout() {
    this.logoutModalSubject.next(true);
  }

  closeLogout() {
    this.logoutModalSubject.next(false);
  }
}