import { NotificationLevel } from "./notification-level";

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
  