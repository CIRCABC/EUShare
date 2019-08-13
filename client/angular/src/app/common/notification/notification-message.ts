/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { NotificationLevel } from './notification-level';

export class NotificationMessage {
  public level: NotificationLevel;
  public body: string;
  public autoclose = false;
  public displayTime = 5;

  public constructor(
    level: NotificationLevel,
    content: string,
    autoclose?: boolean,
    displayTime?: number
  ) {
    this.level = level;
    this.body = content;
    if (autoclose) {
      this.autoclose = autoclose;
    }
    if (displayTime) {
      this.displayTime = displayTime;
    }
  }
}
