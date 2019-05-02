/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
export class Common {
  static dateToISO8601(date: Date) {
    const yyyy = date
      .getFullYear()
      .toString()
      .padStart(4, '0');

    const mm = (date.getMonth() + 1).toString().padStart(2, '0');

    const dd = date
      .getDate()
      .toString()
      .padStart(2, '0');

    return `${yyyy}-${mm}-${dd}`;
  }
}
