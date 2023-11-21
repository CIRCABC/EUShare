/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TranslocoModule } from '@ngneat/transloco';
import { firstValueFrom } from 'rxjs';
import { CaptchaControllerService } from '../../openapi-eu-captcha/api/captchaController.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'cbc-captcha',
  templateUrl: './captcha.component.html',
  styleUrls: ['./captcha.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, TranslocoModule, MatIconModule],
})
export class CaptchaComponent implements OnInit {
  constructor(
    private captchaService: CaptchaControllerService,
    private sanitizer: DomSanitizer
  ) {}

  private capitalizationValue = false;
  @Input()
  get capitalization() {
    return this.capitalizationValue;
  }

  set capitalization(value: boolean) {
    if (this.capitalizationValue !== value) {
      this.capitalizationValue = value;
      this.init();
    }
  }

  private sizeValue = 8;
  @Input()
  get size() {
    return this.sizeValue;
  }

  set size(value: number) {
    if (this.sizeValue !== value) {
      this.sizeValue = value;
      this.init();
    }
  }

  private captchaLanguageId = 'en-GB';
  @Input()
  get languageCode() {
    const result = this.captchaLang.find(
      (captchaLang) => captchaLang.id === this.captchaLanguageId
    );
    if (result) {
      return result.code;
    } else {
      return 'en';
    }
  }
  set languageCode(value: string) {
    const result = this.captchaLang.find(
      (captchaLang) => captchaLang.code === value
    );
    let newCaptchaLanguageId = 'en-GB';
    if (result) {
      newCaptchaLanguageId = result.id;
    }

    if (newCaptchaLanguageId !== this.captchaLanguageId) {
      this.captchaLanguageId = newCaptchaLanguageId;
      this.init();
    }
  }
  captchaImage!: string;
  captchaId!: string;
  captchaToken!: string;
  audioCaptcha!: SafeResourceUrl;
  answer = new FormControl('', [Validators.required, Validators.maxLength(8)]);
  private captchaLang = [
    { code: 'en', id: 'en-GB', name: 'English' },
    { code: 'fr', id: 'fr-FR', name: 'Français' },
    { code: 'de', id: 'de-DE', name: 'Deutsch' },
    { code: 'bg', id: 'bg-BG', name: 'български' },
    { code: 'hr', id: 'hr-HR', name: 'Hrvatski' },
    { code: 'da', id: 'da-DA', name: 'Dansk' },
    { code: 'es', id: 'es-ES', name: 'Español' },
    { code: 'et', id: 'et-ET', name: 'Eesti keel' },
    { code: 'fi', id: 'fi-FI', name: 'Suomi' },
    { code: 'el', id: 'el-EL', name: 'ελληνικά' },
    { code: 'hu', id: 'hu-HU', name: 'Magyar' },
    { code: 'it', id: 'it-IT', name: 'Italiano' },
    { code: 'lv', id: 'lv-LV', name: 'Latviešu valoda' },
    { code: 'lt', id: 'lt-LT', name: 'Lietuvių kalba' },
    { code: 'mt', id: 'mt-MT', name: 'Malti' },
    { code: 'nl', id: 'nl-NL', name: 'Nederlands' },
    { code: 'pl', id: 'pl-PL', name: 'Polski' },
    { code: 'pt', id: 'pt-PT', name: 'Português' },
    { code: 'ro', id: 'ro-RO', name: 'Română' },
    { code: 'sk', id: 'sk-SK', name: 'Slovenčina' },
    { code: 'sl', id: 'sl-SL', name: 'Slovenščina' },
    { code: 'sv', id: 'sv-SV', name: 'Svenska' },
    { code: 'cs', id: 'cs-CS', name: 'čeština' },
  ];

  async ngOnInit() {
    await this.init();
  }

  private async init() {
    const captcha = await firstValueFrom(
      this.captchaService.getCaptchaImageUsingGET(
        this.captchaLanguageId,
        this.sizeValue,
        undefined,
        this.capitalizationValue,
        undefined,
        undefined,
        'response'
      )
    );

    if (captcha.body !== null) {
      this.captchaImage = `data:image/png;base64,${captcha.body.captchaImg}`;
      this.captchaId = captcha.body.captchaId;
      this.audioCaptcha = this.sanitizer.bypassSecurityTrustResourceUrl(
        `data:audio/wav;base64,${captcha.body.audioCaptcha}`
      );
    }

    this.captchaToken = captcha.headers.get('x-jwtString') as string;
  }
  public async refresh() {
    await this.init();
  }
}
