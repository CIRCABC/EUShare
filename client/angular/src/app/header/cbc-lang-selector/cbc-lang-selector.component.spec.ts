import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CbcLangSelectorComponent } from './cbc-lang-selector.component';

describe('CbcLangSelectorComponent', () => {
  let component: CbcLangSelectorComponent;
  let fixture: ComponentFixture<CbcLangSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CbcLangSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CbcLangSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
