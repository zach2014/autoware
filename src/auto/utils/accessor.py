# License for AUTO
# 


__means__ = ["ssh", "telnet", "web", "serial"]

class RCtlBuilder(object):
    """To setup ssh/telnet/web/serial connected session for control network node in test"""
    def __init__(self, mean, remoteHost, **kwargs):
        #: the type of connection to the network node, as a sting should be in __means__
	self.mean = mean
	#: the hostName of remote node, as a string for hostName
	self.remoteHost = remoteHost
	#: the parameters for the specific connection, as a dict, like {"port" : 23, "username": "root", "password": "root", "keyfile": "key_file"}
	slef.params = kwargs

   def connct(self):
	con = None
	if self.mean is "ssh":
	    con = _getSshCon()
	elif self.mean is "telnet":
	    con = _getTelnetCon()
	elif self.mean is "web":
	    con = _getWebCon()
	elif self.mean is "serial":
	    con = _getSerialCon()
	return con

    def _getSshCon(self):
	raise NotImplementedError

    def _getTelnetCon(self):
	raise NotImplementedError

    def _getWebCon(self):
	raise NotImplementedError

    def _getSerialCon(self):
	raise NotImplementedError


	    	
