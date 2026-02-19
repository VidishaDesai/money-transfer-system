import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { ModalService } from './core/services/modal.service';
import { AuthService } from './core/services/auth.service';
import { LogoutConfirm } from './shared/logout-confirm/logout-confirm';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, LogoutConfirm],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('money-transfer-ui');

  showLogoutModal = false;

  constructor(
    private modal: ModalService,
    private auth: AuthService
  ) {
    this.modal.logoutModal$.subscribe(value => {
      this.showLogoutModal = value;
    });
  }
  confirmLogout() {
    this.modal.closeLogout();
    this.auth.logout();
  }

  closeLogout() {
    this.modal.closeLogout();
  }
  
}
