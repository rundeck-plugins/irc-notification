
## Description

This is a Rundeck Notification plugin that will send a message
to an IRC channel. The plugin appears as "RundeckBot" to
the channel, issues a message and then disconnects.

Here's an example message:

    RundeckBot: [START] "say hi" run by alexh: http://Targa.local:4440/execution/follow/129

The message contains the trigger name (START), the job name ("say hi"),
who started it ("alexh") and a URL to follow the execution.

## Build / Deploy

To build the project from source, run: `gradle build`.
The resulting jar will be found in `build/libs`.

Copy the  jar to Rundeck plugins directory. For example, on an RPM installation:

    cp build/libs/irc-notification-1.0.0.jar /var/lib/rundeck/libext

or for a launcher:

    cp build/libs/irc-notification-1.0.0.jar $RDECK_BASE/libext

Then restart the Rundeck service.

## Usage

To use the plugin, configure your job to send a notification
for on start, success or failure. The example job below
sends a notification on start:

```YAML
- id: a3528977-cc67-4d50-97d4-729845c643b9
  name: say hi
  description: this is the hi saying job
  project: examples
  loglevel: INFO
  multipleExecutions: true
  sequence:
    keepgoing: false
    strategy: node-first
    commands:
    - exec: echo "${option.message}"
    - script: |-
        #!/usr/bin/env python
        print 'this is a python script'
  notification:
    onstart:
      plugin:
        type: IRC
        configuration:
          channel: '#mychannel'
          server: irc.acme.com
  uuid: a3528977-cc67-4d50-97d4-729845c643b9
```

## Troubleshooting

Output from the IRC communication can be found in Rundeck's service.log.