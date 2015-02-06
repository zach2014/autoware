#endpoint is a kind of application/service server, can be accessed through network socket, vty terminal, like shell, serial, ssh, web, ftp, telnet, etc.
#
__ALL__ = ["Endpoint", "Service"]
class Endpoint(object):
    """A kind of service point"""
    def __init__(self, pointType=None, *url, **kwargs):
	#: the type of service, as a string
	self.pointType = pointType
	#: the unique identifier for the service, a string or a list, like:
	#: for ssh, telnet, ftp, tftp, scp => hostName/ipAddr:[port]
        #: for shell => /bin/sh
        #: for serial => /dev/ttyS0
        #: for http, https => http[s]://hostName:[port]/path
        self.pointUrl = list(url)
	#: profile to specify parameters for connection to endpoint
	self.profile = dict(kwargs)

    def getService(self):
	"""start up service session"""
	return  _startUp()

    def closeService(self):
	"""tear down service session"""
	return _tearDown()

    def _startUp(self):
	if self.pointType == Service.scp:
	    pass
	if self.pointType == Service.ssh:
	    pass
	if self.pointType == Service.telnet:
	    raise NotImplementedError
	if self.pointType == Service.web:
	    raise NotImplementedError
	if self.pointType == Service.sftp:
	    raise NotImplementedError

    def _tearDown(self):
	pass 


class ServiceMeta(type):
    @property
    def ssh(cls):
	return cls.sshname

    @property
    def telnet(cls):
	return cls.telnetname

    @property
    def web(cls):
	return cls.webname

    @property
    def scp(cls):
	return cls.scpname

    @property
    def sftp(cls):
	return cls.sftpname

class Service(object):
    """Service typs collection to use directly, like Service.ssh"""
    __metaclass__=ServiceMeta
    sshname = "ssh"
    telnetname = "telnet"
    webname = "web"
    scpname = "scp"
    sftpname = "sftp"
  
