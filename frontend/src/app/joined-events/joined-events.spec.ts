import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JoinedEvents } from './joined-events';

describe('JoinedEvents', () => {
  let component: JoinedEvents;
  let fixture: ComponentFixture<JoinedEvents>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JoinedEvents],
    }).compileComponents();

    fixture = TestBed.createComponent(JoinedEvents);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
