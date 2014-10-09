#Lisence for AUTO
import subprocess
		

class Node(object):
	"""Basic network node in test setup"""
	def __init__(self, name, hostName=None, desc='', **kwProfiles):
		#: Node name to be identifier, as a string required
		self.name = name
		#: ip addr of Node to be access, as a string like 'x.x.x.x'
		self.hostName = hostName
		#: the available services set, like ssh, telnet, web, etc., a dict as belows
		#: {"ssh": {ssh_profile}, "telnet": {telnet_profile}, "web": {web_profile}}
		self.services = dict(**kwProfiles)
		#: the description for the Node
		self.desc = desc

	def updateServices(self, servicesSet):
		if servicesSet:
			self.services.update(servicesSet)
		else:
			raise TypeError

	def has_service(self, service):
		return self.services.has_key(service)

	def getProfileOf(self, service):
		return self.profiles.get(service)

	def clearServices(self):
		self.services.clear();

	def shell(self, *args):
		if self.has_service("shell"):
			return subprocess.call(*args, shell=True)
		elif self.has_service("ssh"):
			#prepare cdm=ssh user@hostname ... from profile_ssh
			#ssh = subprocess.Popen(cmd)
			#return ssh.communicate(*args)
			raise NotImplementedError
			return 
		elif self.has_service("telnet"):
			raise NotImplementedError
			return 
		elif self.has_service("serial"):
			raise NotImplementedError
			return

