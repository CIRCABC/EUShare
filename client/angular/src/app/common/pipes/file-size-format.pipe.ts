import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fileSizeFormat'
})
export class FileSizeFormatPipe implements PipeTransform {

  transform(value: number): string {
    if (isNaN(value)) { return ' ERRORINTHEVALUE' }
    if (value >= 1024) {
      const valueInKb = Math.floor(value / 1024);
      if (valueInKb >= 1024) {
        const valueInMb = Math.floor(valueInKb / 1024);
        if (valueInMb >= 1024) {
          const valueInGb = Math.floor(valueInMb / 1024);
          return valueInGb + ' GigaBytes';
        } else {
          return valueInMb + ' MegaBytes'
        }
      } else {
        return valueInKb + ' KiloBytes';
      }
    } else {
      return value + ' Bytes';
    }
  }

}
