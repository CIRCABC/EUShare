import { Component, OnInit, Input } from '@angular/core';
import { FileInfoRecipient } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { faFile, faLock } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-download-file-row',
  templateUrl: './download-file-row.component.html',
  styleUrls: ['./download-file-row.component.scss']
})
export class DownloadFileRowComponent implements OnInit {

  @Input('fileToDisplay')
  fileToDisplay!: FileInfoRecipient;

  public isMoreDisplayed = false;
  public faFile = faFile;
  public faLock = faLock;

  constructor(private modalService: ModalsService) { }

  ngOnInit() {
  }

  public displayMore() {
    this.isMoreDisplayed = true;
  }

  public displayLess() {
    this.isMoreDisplayed = false;
  }

  public openDownloadModal(
  ) {
    this.modalService.activateDownloadModal(this.fileToDisplay.fileId, this.fileToDisplay.name, this.fileToDisplay.hasPassword);
  }

}
