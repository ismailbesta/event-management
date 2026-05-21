import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchivedEvents } from './archived-events';

describe('ArchivedEvents', () => {
  let component: ArchivedEvents;
  let fixture: ComponentFixture<ArchivedEvents>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArchivedEvents],
    }).compileComponents();

    fixture = TestBed.createComponent(ArchivedEvents);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
