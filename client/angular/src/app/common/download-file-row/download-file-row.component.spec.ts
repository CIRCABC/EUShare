import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadFileRowComponent } from './download-file-row.component';

describe('DownloadFileRowComponent', () => {
  let component: DownloadFileRowComponent;
  let fixture: ComponentFixture<DownloadFileRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DownloadFileRowComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadFileRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
