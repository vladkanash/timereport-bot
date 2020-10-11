package org.vladkanash.dao;

import dagger.Binds;
import dagger.Module;

@Module
public interface DaoModule {

    @Binds
    UserDao bindUserDao(AirtableUserDao airtableUserDao);
}
