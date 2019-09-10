import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadFileRowContainerComponent } from './download-file-row-container.component';

describe('DownloadFileRowContainerComponent', () => {
  let component: DownloadFileRowContainerComponent;
  let fixture: ComponentFixture<DownloadFileRowContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DownloadFileRowContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadFileRowContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
