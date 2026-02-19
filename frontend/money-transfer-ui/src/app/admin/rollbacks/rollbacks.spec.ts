import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Rollbacks } from './rollbacks';

describe('Rollbacks', () => {
  let component: Rollbacks;
  let fixture: ComponentFixture<Rollbacks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Rollbacks]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Rollbacks);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
