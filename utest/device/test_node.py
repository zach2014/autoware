#unit test for auto.device.node
import unittest
from auto.device import node


class TestNode(unittest.TestCase):
    def setUp(self):
	self.sh_service = {"shell": {}}
	self.ssh_service = {"ssh": {"profile_ssh_only"}}
	self.node_default = node.Node(name="node_default")
	self.node_identified = node.Node(name='node_192.168.1.1', hostName ='192.168.1.1', desc="this is a router")
	self.node_has_service = node.Node(name='service_node',**self.ssh_service)

    def test_init_deaulf_args(self):
	self.assertIsNotNone(self.node_default)
	self.assertEqual(self.node_default.hostName, None)
	self.assertEqual(self.node_default.desc, '')
	self.assertEqual(self.node_default.services, {})
	
    def test_init_all_args(self):
	self.assertEqual(self.node_identified.name, 'node_192.168.1.1')
	self.assertEqual(self.node_identified.hostName, '192.168.1.1')
	self.assertEqual(self.node_identified.services, {})
	self.assertEqual(self.node_identified.desc, 'this is a router')

    def test_init_with_kwargs(self):
	self.assertDictContainsSubset(self.ssh_service, self.node_has_service.services)
	self.assertEqual(1, len(self.node_has_service.services))

    def test_updateServices(self):
	self.all_services = {"ssh": "profile_ssh", "telnet": "profile_telnet",
			     "web": "profile_web", "serial": "profile_serial"
			    }
	self.node_default.updateServices(self.all_services)
	self.assertEqual(self.node_default.services, self.all_services)
	self.node_default.updateServices(self.ssh_service)
	self.assertDictContainsSubset(self.ssh_service, self.node_default.services)
	self.assertEqual(len(self.node_default.services), 4)
	
    def test_updateServices_none(self):
	self.assertRaises(TypeError, self.node_default.updateServices, None)

    def test_clearServices(self):
	self.node_default.clearServices()
	self.assertEqual(self.node_default.services, {})

    def test_shell(self):
	self.node_default.updateServices(self.sh_service)
	retcode = self.node_default.shell("pwd")
	self.assertEqual(retcode, 0)
	

if __name__ == '__main__':
    unittest.main()

