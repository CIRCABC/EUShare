/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import com.circabc.easyshare.api.TestHelper;
import com.circabc.easyshare.storage.DBFile;
import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.DBUserFile;
import com.circabc.easyshare.storage.FileRepository;
import com.circabc.easyshare.storage.UserFileRepository;
import com.circabc.easyshare.storage.UserRepository;
import com.circabc.easyshare.utils.StringUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.emory.mathcs.backport.java.util.Collections;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DBTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserFileRepository userFileRepository;


    @Test
    @Transactional
    public void creationOfaUserTest() {
        DBUser dbUser = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername"); //NOSONAR
        userRepository.save(dbUser);
        assertEquals(dbUser, userRepository.findOneByEmail("emailA@email.com"));
        assertEquals(dbUser, userRepository.findOneByName("uniqueName"));
        assertEquals(dbUser, userRepository.findOneByUsername("uniqueUsername"));
    }

    @Test
    @Transactional
    public void creationOfanExternalUserTest() {
        DBUser dbUser = DBUser.createExternalUser("emailA@email.com", null);
        dbUser = userRepository.save(dbUser);
        assertEquals(dbUser, userRepository.findOneByEmail("emailA@email.com"));
        assertEquals(dbUser, userRepository.findOneByName(null));
        assertEquals(dbUser, userRepository.findOneByUsername(null));
    }

    @Test
    @Transactional
    public void creationOfaFileTest() {
        DBUser dbUser = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
        userRepository.save(dbUser);
        DBFile dbFile = new DBFile("id", dbUser, Collections.emptySet(), "filename", 1024, LocalDate.now(), "/a/sample/path"); //NOSONAR
        fileRepository.save(dbFile);
    }

    @Test
    @Transactional
    public void creationOfaUserToFileTest() {
        DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
        userRepository.save(uploader);

        DBFile dbFile = new DBFile("id", uploader, Collections.emptySet(), "filename", 1024, LocalDate.now(), "/a/sample/path");
        DBUser shareUser = DBUser.createExternalUser("emailExternal@email.com", null); //NOSONAR
        DBUserFile dbUserFile = new DBUserFile(StringUtils.randomString(), shareUser, dbFile);

        shareUser.getFilesReceived().add(dbUserFile);
        dbFile.getSharedWith().add(dbUserFile);
        userRepository.save(shareUser);
        fileRepository.save(dbFile);

        DBUser uploaderSaved = userRepository.findOneByEmail("emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBUser shareUserSaved = userRepository.findOneByEmail("emailExternal@email.com");
        assertEquals(shareUser, shareUserSaved);
        assertEquals(shareUser.getFilesReceived().iterator().next(), dbUserFile);
        assertEquals(userFileRepository.findOneByDownloadId(dbUserFile.getDownloadId()), shareUserSaved.getFilesReceived().iterator().next());
    }

    @Test
    @Transactional
    public void searchOfAFileByReceiver() {
        DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
        userRepository.save(uploader);
        DBFile dbFile = new DBFile("id", uploader, Collections.emptySet(), "filename", 1024, LocalDate.now(), "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        DBUser shareUser = DBUser.createExternalUser("email2@email.com", "name");
        DBUserFile dbUserFile = new DBUserFile(StringUtils.randomString(), shareUser, dbFile);

        shareUser.getFilesReceived().add(dbUserFile);
        dbFile.getSharedWith().add(dbUserFile);
        userRepository.save(shareUser);
        fileRepository.save(dbFile);
        List<DBFile> shareUsersFiles = fileRepository.findByStatusAndSharedWith_Receiver_Id(DBFile.Status.AVAILABLE, shareUser.getId(), PageRequest.of(0,10));

        assertEquals(1, shareUsersFiles.size());
        assertEquals(dbFile, shareUsersFiles.get(0));
    }

    @Test
    @Transactional
    public void searchOfAFileByReceiverWhereNothingIsThere() {
        List<DBFile> shareUsersFiles = fileRepository.findByStatusAndSharedWith_Receiver_Id(DBFile.Status.AVAILABLE, "fakeID", PageRequest.of(1,10));
        assertNotNull(shareUsersFiles);
        assertEquals(0, shareUsersFiles.size());
    }

    @Test
    @Transactional
    public void searchOfAFileByUploader() {
        DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
        userRepository.save(uploader);
        DBFile dbFile = new DBFile("id", uploader, Collections.emptySet(), "filename", 1024, LocalDate.now(), "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        DBUser shareUser = DBUser.createExternalUser("email2@email.com", "name");
        DBUserFile dbUserFile = new DBUserFile(StringUtils.randomString(), shareUser, dbFile);

        shareUser.getFilesReceived().add(dbUserFile);
        dbFile.getSharedWith().add(dbUserFile);
        userRepository.save(shareUser);
        fileRepository.save(dbFile);
        List<DBFile> shareUsersFiles = fileRepository.findByStatusAndUploader_Id(DBFile.Status.AVAILABLE, uploader.getId(), PageRequest.of(0,10));
        assertEquals(1, shareUsersFiles.size());
        assertEquals(dbFile, shareUsersFiles.get(0));
    }

    @Test
    @Transactional
    public void searchOfUsersByEmail() {
        DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
        userRepository.save(uploader);

        List<DBUser> foundUsers = userRepository.findByEmailStartsWith("emailA", PageRequest.of(0,10));
        assertEquals(1, foundUsers.size());
        assertEquals(uploader, foundUsers.get(0));
    }

}