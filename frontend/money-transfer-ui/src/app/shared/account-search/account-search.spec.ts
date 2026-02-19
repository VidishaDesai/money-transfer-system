import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountSearch } from './account-search';

describe('AccountSearch', () => {
  let component: AccountSearch;
  let fixture: ComponentFixture<AccountSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountSearch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountSearch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
