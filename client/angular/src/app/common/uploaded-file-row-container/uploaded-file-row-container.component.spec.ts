import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FileRowContainerComponent } from './uploaded-file-row-container.component';

describe('FileRowContainerComponent', () => {
  let component: FileRowContainerComponent;
  let fixture: ComponentFixture<FileRowContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FileRowContainerComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FileRowContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
