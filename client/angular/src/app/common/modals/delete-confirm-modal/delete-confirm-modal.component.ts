import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';

@Component({
  selector: 'app-delete-confirm-modal',
  templateUrl: './delete-confirm-modal.component.html',
  styleUrls: ['./delete-confirm-modal.component.scss']
})
export class DeleteConfirmModalComponent implements OnInit {
  public modalActive!: boolean;
  public modalFileId!: string;
  public modalFileName!: string;

  constructor(private modalService: ModalsService) { }

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activateDeleteConfirmModal$.subscribe(nextModalActiveValue => {
      this.modalActive = nextModalActiveValue.modalActive;
      this.modalFileId = nextModalActiveValue.modalFileId;
      this.modalFileName = nextModalActiveValue.modalFileName;
    });
  }

  public closeModal() {
    this.modalService.deactivateDeleteConfirmModal();
  }

}
