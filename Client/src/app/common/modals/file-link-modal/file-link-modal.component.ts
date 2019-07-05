import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { NotificationService } from '../../notification/notification.service';

@Component({
  selector: 'app-file-link-modal',
  templateUrl: './file-link-modal.component.html'
})
export class FileLinkModalComponent implements OnInit {
  public modalActive: boolean = false;
  public fileLink!: string;
  public isLoading: boolean = false;

  constructor(private modalService: ModalsService, private notificationService: NotificationService) { }

  ngOnInit() {
    this.modalService.activateFileLinkModal$.subscribe(nextModalActiveValue => {
      this.modalActive = nextModalActiveValue.modalActive;
      this.fileLink = nextModalActiveValue.fileLink;
    });
  }

  copyLink(inputElement: any) {
    this.isLoading = true;
    inputElement.select();
    document.execCommand('copy');
    inputElement.setSelectionRange(0, 0);
    this.notificationService.addSuccessMessage('Copied file link !', true);
    setTimeout(() => {
      this.isLoading = false
    }, 300);
  }

  closeModal() {
    this.modalService.deactivateFileLinkModal();
  }

}
