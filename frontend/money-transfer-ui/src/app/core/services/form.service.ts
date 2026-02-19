import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FormService {

  setSuccess(component: any, msg: string): void {
    component.message = msg;
    component.messageType = 'success';
    component.loading = false;
  }

  setError(component: any, err: any, fallback = 'Operation failed'): void {
    component.message =
      err?.error?.message ||
      this.extractValidationErrors(err) ||
      fallback;

    component.messageType = 'error';
    component.loading = false;
  }

  private extractValidationErrors(err: any): string | null {
    if (!err?.error?.errors) return null;
    return Object.values(err.error.errors).join(', ');
  }
}