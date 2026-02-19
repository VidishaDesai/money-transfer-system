import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-logout-confirm',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './logout-confirm.html',
  styleUrl: './logout-confirm.css'
})
export class LogoutConfirm {

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  onConfirm() {
    this.confirm.emit();
  }

  onCancel() {
    this.cancel.emit();
  }
}