/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.circabc.eushare.storage.DBFile;
import eu.europa.circabc.eushare.storage.DBUser;
import eu.europa.circabc.eushare.storage.DBShare;
import eu.europa.circabc.eushare.storage.FileRepository;
import eu.europa.circabc.eushare.storage.ShareRepository;
import eu.europa.circabc.eushare.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DBTest {

        @Autowired
        UserRepository userRepository;

        @Autowired
        FileRepository fileRepository;

        @Autowired
        ShareRepository shareRepository;

        @Test
        @Transactional
        public void creationOfaFileTest() {
                DBUser dbUser = DBUser.createUser("emailA@email.com", "uniqueName", 1024, "uniqueUsername",
                                DBUser.Role.INTERNAL);
                userRepository.save(dbUser);
                DBFile dbFile = new DBFile(dbUser, Collections.emptySet(), "filename", 1024, LocalDate.now(),
                                "/a/sample/path"); // NOSONAR
                fileRepository.save(dbFile);
                // Verify insertion
                DBFile dbFileSaved = fileRepository.findOneById(dbFile.getId());
                assertEquals(dbFile, dbFileSaved);
        }

        /*
         * @Test
         * 
         * @Transactional
         * public void creationOfaUserToFileTest() {
         * DBUser uploader = DBUser.createUser("emailA@email.com", "uniqueName", 1024,
         * "uniqueUsername");
         * userRepository.save(uploader);
         * 
         * // Verify insertion
         * DBUser uploaderSaved =
         * userRepository.findOneByEmailIgnoreCase("emailA@email.com");
         * assertEquals(uploader, uploaderSaved);
         * 
         * DBFile dbFile = new DBFile("id", uploader, Collections.emptySet(),
         * "filename", 1024, LocalDate.now(),
         * "/a/sample/path");
         * fileRepository.save(dbFile);
         * 
         * // Verify insertion
         * DBFile dbFileSaved = fileRepository.findOneById("id");
         * assertEquals(dbFile, dbFileSaved);
         * 
         * 
         * 
         * DBShare dbShare = new DBShare("downloadId","emailExternal@email.com",
         * dbFile);
         * shareRepository.save(dbShare);
         * 
         * // Verify insertion
         * DBShare dbShareSaved = shareRepository.findOneByDownloadId("downloadId");
         * assertEquals(new DBShare("downloadId", "emailExternal@email.com", dbFile),
         * dbShareSaved);
         * 
         * // Verify deletion
         * // shareRepository.deleteByDownloadId("downloadId");
         * shareRepository.deleteByReceiverAndFile_id("emailExternal@email.com",
         * dbFileSaved.getId());
         * DBShare dbShareDeleted = shareRepository.findOneByDownloadId("downloadId");
         * assertEquals(null, dbShareDeleted);
         * 
         * DBUser shareUserSavedWithSharedFileDeleted =
         * userRepository.findOneByEmailIgnoreCase("emailExternal@email.com");
         * assertEquals(0,
         * shareUserSavedWithSharedFileDeleted.getFilesReceived().size());
         * }
         */

        @Test
        @Transactional
        public void searchOfAFileByReceiver() {
                DBUser uploader = DBUser.createUser("emailA@email.com", "uniqueName", 1024,
                                "uniqueUsername", DBUser.Role.INTERNAL);
                userRepository.save(uploader);

                DBFile dbFile = new DBFile(uploader, Collections.emptySet(), "filename", 1024, LocalDate.now(),
                                "/a/sample/path");
                dbFile.setStatus(DBFile.Status.AVAILABLE);
                fileRepository.save(dbFile);

                DBShare dbShare = new DBShare("email2@email.com", dbFile, "message", true);
                dbShare.setShorturl("AAAAAA");
                shareRepository.save(dbShare);

                List<DBFile> shareUsersFiles = fileRepository.findByStatusAndSharedWithEmail(DBFile.Status.AVAILABLE,
                                "email2@email.com", PageRequest.of(0, 10));

                assertEquals(1, shareUsersFiles.size());
                assertEquals(dbFile, shareUsersFiles.get(0));
        }

        @Test
        @Transactional
        public void searchOfAFileByReceiverWhereNothingIsThere() {
                List<DBFile> shareUsersFiles = fileRepository.findByStatusAndSharedWithEmail(DBFile.Status.AVAILABLE,
                                "fakeID", PageRequest.of(1, 10));
                assertNotNull(shareUsersFiles);
                assertEquals(0, shareUsersFiles.size());
        }

        @Test
        @Transactional
        public void searchOfAFileByUploader() {
                DBUser uploader = DBUser.createUser("emailA@email.com", "uniqueName", 1024,
                                "uniqueUsername", DBUser.Role.INTERNAL);
                userRepository.save(uploader);

                DBFile dbFile = new DBFile(uploader, Collections.emptySet(), "filename", 1024, LocalDate.now(),
                                "/a/sample/path");
                dbFile.setStatus(DBFile.Status.AVAILABLE);
                fileRepository.save(dbFile);

                DBShare dbShare = new DBShare("email2@email.com", dbFile, "message", true);
                dbShare.setShorturl("AAAAAA");
                shareRepository.save(dbShare);

                List<DBFile> shareUsersFiles = fileRepository.findByStatusAndUploaderId(DBFile.Status.AVAILABLE,
                                uploader.getId(), PageRequest.of(0, 10));
                assertEquals(1, shareUsersFiles.size());
                assertEquals(dbFile, shareUsersFiles.get(0));
        }

        @Test
        @Transactional
        public void searchOfUsersByEmail() {
                DBUser uploader = DBUser.createUser("emailA@email.com", "uniqueName", 1024,
                                "uniqueUsername", DBUser.Role.INTERNAL);
                userRepository.save(uploader);

                List<DBUser> foundUsers = userRepository.findByEmailIgnoreCaseStartsWith("emailA",
                                PageRequest.of(0, 10));
                assertEquals(1, foundUsers.size());
                assertEquals(uploader, foundUsers.get(0));
        }

        @Test
        @Transactional
        public void searchOfAFileByReceiverAndOrderByExpDateAndName() {
                DBUser uploader = DBUser.createUser("emailA@email.com", "uniqueName", 1024,
                                "uniqueUsername", DBUser.Role.INTERNAL);
                userRepository.save(uploader);

                DBFile dbFile = new DBFile(uploader, Collections.emptySet(), "filename", 1024, LocalDate.now(),
                                "/a/sample/path");
                dbFile.setStatus(DBFile.Status.AVAILABLE);
                fileRepository.save(dbFile);

                DBFile dbFile2 = new DBFile(uploader, Collections.emptySet(), "filename", 1024,
                                LocalDate.now().plusDays(1), "/a/sample/path");
                dbFile2.setStatus(DBFile.Status.AVAILABLE);
                fileRepository.save(dbFile2);

                DBFile dbFile3 = new DBFile(uploader, Collections.emptySet(), "zfilenam", 1024,
                                LocalDate.now().plusDays(1), "/a/sample/path");
                dbFile3.setStatus(DBFile.Status.AVAILABLE);
                fileRepository.save(dbFile3);

                DBShare dbShare = new DBShare("email2@email.com", dbFile, "message", true);
                dbShare.setShorturl("AAAAAA");
                shareRepository.save(dbShare);
                List<DBFile.Status> status = new ArrayList<DBFile.Status>();
                status.add(DBFile.Status.AVAILABLE);
                List<DBFile> shareUsersFiles = fileRepository
                                .findByStatusInAndUploaderIdOrderByExpirationDateAscFilenameAsc(
                                                status, uploader.getId(), PageRequest.of(0, 10));

                DBFile[] expectedShareUsersFiles = { dbFile, dbFile2, dbFile3 };
                assertEquals(3, shareUsersFiles.size());
                assertArrayEquals(expectedShareUsersFiles, shareUsersFiles.toArray());
        }

}