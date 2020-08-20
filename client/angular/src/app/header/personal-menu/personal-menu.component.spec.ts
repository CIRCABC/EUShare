import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalMenuComponent } from './personal-menu.component';

describe('PersonalMenuComponent', () => {
  let component: PersonalMenuComponent;
  let fixture: ComponentFixture<PersonalMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PersonalMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
