import { Component, OnInit } from '@angular/core';
import { UsersService, UserInfo } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { faUser, faUserTie } from '@fortawesome/free-solid-svg-icons';
@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.css']
})
export class AdministrationComponent implements OnInit {
  public faUser = faUser;
  public faUserTie = faUserTie;

  public searchIsLoading = false;
  public isAfterSearch = false;
  public isAfterSelected = false;
  public searchString = '';

  public pageSize = 10;
  public pageNumber = 0;

  public userInfoArray: Array<UserInfo> = new Array();
  public userInfoArrayNext: Array<UserInfo> = new Array();
  public userInfoArrayPrevious: Array<UserInfo> = new Array();

  public selectedUserInfoIndex = 0;

  public valuesInGigaBytes = [
    0,
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    20,
    25,
    30,
    35,
    40,
    45,
    50,
    60,
    70,
    80,
    100,
    1024
  ];
  public selectedValueInGigaBytesIndex = 0;

  public selectedValueInGigaBytes = 0;
  public changeIsLoading = false;

  constructor(
    private usersApi: UsersService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {}

  minSize(n1: number, n2: number) {
    return n1 < n2 ? n1 : n2;
  }

  valueInGigCorresponds(valueInGig: number, valueInBytes: number): boolean {
    const valueInBytesToGig = Math.floor((valueInBytes / 1024) * 1024 * 1024);
    return valueInBytesToGig === valueInGig;
  }

  public async resultsNextPage() {
    this.isAfterSelected = false;
    this.pageNumber++;
    this.userInfoArrayPrevious = this.userInfoArray;
    this.userInfoArray = this.userInfoArrayNext;

    if (this.userInfoArrayNext.length >= this.pageSize) {
      this.userInfoArrayNext = new Array();
      this.userInfoArrayNext = await this.usersApi
        .getUsersUserInfo(this.pageSize, this.pageNumber + 1, this.searchString)
        .toPromise();
    }
  }

  public async resultsPreviousPage() {
    this.isAfterSelected = false;
    this.pageNumber--;
    this.userInfoArrayNext = this.userInfoArray;
    this.userInfoArray = this.userInfoArrayPrevious;

    if (this.pageNumber - 1 >= 0) {
      this.userInfoArrayPrevious = await this.usersApi
        .getUsersUserInfo(this.pageSize, this.pageNumber - 1, this.searchString)
        .toPromise();
    }
  }

  public async search() {
    try {
      this.searchIsLoading = true;
      this.pageNumber = 0;
      this.isAfterSearch = false;
      this.isAfterSelected = false;

      this.userInfoArrayNext = new Array();
      this.userInfoArrayPrevious = new Array();

      this.userInfoArray = await this.usersApi
        .getUsersUserInfo(this.pageSize, this.pageNumber, this.searchString)
        .toPromise();
      if (this.userInfoArray.length >= this.pageSize) {
        this.userInfoArrayNext = await this.usersApi
          .getUsersUserInfo(
            this.pageSize,
            this.pageNumber + 1,
            this.searchString
          )
          .toPromise();
      }
      this.isAfterSearch = true;
    } catch (error) {
      this.notificationService.errorMessageToDisplay(
        error,
        'searching for the users'
      );
    } finally {
      this.searchIsLoading = false;
    }
  }

  public displayUserInfoNumber(i: number) {
    this.isAfterSelected = true;
    this.selectedUserInfoIndex = i;
    this.selectedValueInGigaBytes = Math.floor(
      this.userInfoArray[i].totalSpace / (1024 * 1024 * 1024)
    );
  }

  public get selectedUserInfo(): UserInfo {
    return this.userInfoArray[this.selectedUserInfoIndex];
  }

  public async change() {
    try {
      this.changeIsLoading = true;
      this.selectedUserInfo.totalSpace =
        this.selectedValueInGigaBytes * 1024 * 1024 * 1024;
      await this.usersApi
        .putUserUserInfo(this.selectedUserInfo.id, this.selectedUserInfo)
        .toPromise();
      this.notificationService.addSuccessMessage(
        'Your change was applied!',
        true
      );
    } catch (error) {
      this.notificationService.errorMessageToDisplay(
        error,
        'updating the user information'
      );
    } finally {
      this.changeIsLoading = false;
    }
  }
}
