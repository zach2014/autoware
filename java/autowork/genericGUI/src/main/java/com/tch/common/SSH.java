package com.tch.common;

import com.jcraft.jsch.Session;

public interface SSH {

	Session openSSH(Account ssh_Account, String host);
}
