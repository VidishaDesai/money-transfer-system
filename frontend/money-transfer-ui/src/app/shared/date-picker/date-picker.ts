import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-date-picker',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './date-picker.html',
  styleUrls: ['./date-picker.css']
})
export class DatePicker {

  @Output() dateSelected = new EventEmitter<string>();

  today = new Date();
  viewDate = new Date();

  weekdays = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
  monthNames = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];

  get daysInMonth(): number {
    return new Date(
      this.viewDate.getFullYear(),
      this.viewDate.getMonth() + 1,
      0
    ).getDate();
  }

  selectDay(day: number) {
    const selected = new Date(
      this.viewDate.getFullYear(),
      this.viewDate.getMonth(),
      day
    );

    const iso = selected.toISOString().split('T')[0];
    this.dateSelected.emit(iso);
  }

  prevMonth() {
    this.viewDate = new Date(
      this.viewDate.getFullYear(),
      this.viewDate.getMonth() - 1,
      1
    );
  }

  nextMonth() {
    this.viewDate = new Date(
      this.viewDate.getFullYear(),
      this.viewDate.getMonth() + 1,
      1
    );
  }

  get daysArray() {
    return Array.from({ length: this.daysInMonth }, (_, i) => i + 1);
  }
}