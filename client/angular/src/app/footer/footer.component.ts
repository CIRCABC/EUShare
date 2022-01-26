

import { Component, Inject } from '@angular/core';
import { environment } from '../../environments/environment';



@Component({
  selector: 'cbc-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
  preserveWhitespaces: true,
})
export class FooterComponent {
  public appAlfVersion = '';
  public appVersion = '0.1';
  public nodeName = '';
  public buildDate = '';
  public circabc_url: string = environment.circabc_url;
  
  constructor(

  ) {
  }
}
