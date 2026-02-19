import {
  Component,
  EventEmitter,
  OnInit,
  Input,
  Output,
  ElementRef,
  ViewChild,
  HostListener
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Account {
  id: number;
  holderName: string;
}

@Component({
  selector: 'app-account-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './account-search.html'
})
export class AccountSearch implements OnInit {

  @Input() apiUrl!: string;
  @Input() excludeCurrentUser = false;

  @Output() accountSelected = new EventEmitter<Account>();

  @ViewChild('searchBox') searchBoxRef!: ElementRef;

  searchTerm = '';
  allAccounts: Account[] = [];
  filteredAccounts: Account[] = [];
  selectedAccount: Account | null = null;

  showDropdown = false;
  highlightedIndex = -1;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.http.get<Account[]>(this.apiUrl)
      .subscribe(accounts => {

        const currentUserId = Number(localStorage.getItem('accountId'));

        this.allAccounts = accounts.filter(acc => {

          if (acc.id === 1) return false; // hide admin

          if (this.excludeCurrentUser && acc.id === currentUserId) {
            return false;
          }

          return true;
        });
      });
  }

  onSearchChange(): void {

    const term = this.searchTerm.toLowerCase().trim();
    this.highlightedIndex = -1;
    this.selectedAccount = null;

    if (!term) {
      this.filteredAccounts = [];
      this.showDropdown = false;
      return;
    }

    this.filteredAccounts = this.allAccounts.filter(acc =>
      acc.id.toString().includes(term) ||
      acc.holderName.toLowerCase().includes(term)
    );

    this.showDropdown = true;
  }

  selectAccount(acc: Account): void {
    this.selectedAccount = acc;
    this.searchTerm = `${acc.id} - ${acc.holderName}`;
    this.showDropdown = false;

    this.accountSelected.emit(acc);
  }

  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {
    if (!this.searchBoxRef) return;

    if (!this.searchBoxRef.nativeElement.contains(event.target)) {
      this.showDropdown = false;
    }
  }

  handleKeyDown(event: KeyboardEvent): void {

    if (!this.showDropdown) return;

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        if (this.highlightedIndex < this.filteredAccounts.length - 1) {
          this.highlightedIndex++;
        }
        break;

      case 'ArrowUp':
        event.preventDefault();
        if (this.highlightedIndex > 0) {
          this.highlightedIndex--;
        }
        break;

      case 'Enter':
        event.preventDefault();
        if (this.highlightedIndex >= 0) {
          this.selectAccount(this.filteredAccounts[this.highlightedIndex]);
        }
        break;

      case 'Escape':
        this.showDropdown = false;
        break;
    }
  }

  clearSelection(): void {
    this.searchTerm = '';
    this.selectedAccount = null;
    this.filteredAccounts = [];
    this.accountSelected.emit(null as any);
  }
}