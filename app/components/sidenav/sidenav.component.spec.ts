import { ComponentFixture, TestBed } from '@angular/core/testing';

import { sidenavComponent } from './sidenav.component';

describe('sidenavComponent', () => {
  let component: sidenavComponent;
  let fixture: ComponentFixture<sidenavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [sidenavComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(sidenavComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
