import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogoutConfirm } from './logout-confirm';

describe('LogoutConfirm', () => {
  let component: LogoutConfirm;
  let fixture: ComponentFixture<LogoutConfirm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogoutConfirm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LogoutConfirm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
