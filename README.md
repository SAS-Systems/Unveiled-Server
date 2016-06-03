# Unveiled-Server (Java-Stack)
[![Build Status](https://travis-ci.org/SAS-Systems/Unveiled-Server.svg?branch=master)](https://travis-ci.org/SAS-Systems/Unveiled-Server)
[![Coverage Status](https://coveralls.io/repos/github/SAS-Systems/Unveiled-Server/badge.svg?branch=master)](https://coveralls.io/github/SAS-Systems/Unveiled-Server?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/97ac7ec068ee4eb787fae263c45829f7)](https://www.codacy.com/app/sebastian-schmidl/Unveiled-Server?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SAS-Systems/Unveiled-Server&amp;utm_campaign=Badge_Grade)


## API Description
The following URLs can be used to interact with the Java-Stack of the Unveiled-Server:

| URL | Method | Header | Paremeters | Description |
|-----|--------|--------|------------|-------------|
|`{unveiled.base}/UploadFile` | POST | user:Int(ID-reference),<br/> token:String | latitude:Double,<br/> longitude:Double,<br/> public:Boolean,<br/> file:&lt;BinaryData&gt; | Is used to upload a file to the server. The parameters and the file content must be send via a multipart body (see screenshot below). |
||||

![Postman screenshot of the UploadFile](https://raw.githubusercontent.com/SAS-Systems/Unveiled-Server/master/UploadFilePostman.png)
