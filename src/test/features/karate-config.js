
function() {
  var env = karate.env;
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev';
  }
  
  var fileConfigs = {
      inputDirectoryPath: 'mnt/igia',
      inputFileName: 'a01.txt',
      inputCSVFileName: 'data_001.csv',
      inputHL7FileName: 'a01.hl7',
      outputDirectoryPath: 'mnt/igia/output',
      outputFileName: 'a01.txt',
      outputCSVFileName: 'data_001.csv',
      outputHL7FileName: 'a01.hl7'
  }
  
  var sftpConfigs = {
      inputHostname: 'integration-app-sftp',
      inputPort: '22',
      inputUsername: 'igia',
      inputPassword: 'integration',
      inputDirectory: '/home/igia/sftp-input',
      inputFileName: 'test.csv',
      outputHostname: 'integration-app-sftp',
      outputPort: '22',
      outputUsername: 'igia',
      outputPassword: 'integration',
      outputDirectory: '/home/igia/sftp-output',
      outputFileName: 'test_output.csv'
  }
  
  var httpConfigs = {
     inputHostname: '0.0.0.0',
     inputPort: '12055',
     inputUri: '/localhost',
     outputHostname: 'localhost',
     outputPort: '80',
     outputUri: '/test'
  }
  
  var mllpConfigs = {
     inputHostname: 'localhost',
     inputPort: '1240',
     outputHostname: 'localhost',
     outputPort: '1241'
  }
  
  var config = {
    baseUrl: 'http://localhost:8050/api',
    tokenUrl: 'http://localhost:9080/auth/realms/igia/protocol/openid-connect/token',
    clientId: 'internal',
    clientSecret: 'internal',
    fileConfigs : fileConfigs,
    sftpConfigs: sftpConfigs,
    httpConfigs: httpConfigs,
    mllpConfigs: mllpConfigs
  };
  
  

  karate.configure('connectTimeout', 10000);
  karate.configure('readTimeout', 10000);
  karate.configure('logPrettyRequest', true);
  return config;
}
