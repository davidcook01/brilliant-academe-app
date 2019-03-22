function vidControls(){
    $(document).ready(function() {
        function Video(playing) {
            this.nowPlaying = playing;
            const videoEl = $("#video").get(0);
            const progressBar = $("#progressBar").get(0);
            const skipTime = 10; // seconds for fast-forward/rewind
            let vidNum = 0;
            const videoTitle = $("#videoTitle").get(0);
            this.anArray = [];
            
            // this.getCourses = async () => {
            //     const proxyurl = "https://cors-anywhere.herokuapp.com/";
            //     const url = "https://ir3v0f4teh.execute-api.us-east-1.amazonaws.com/ba-api/course/46efdce8-6a69-4e69-aa8b-fcc0aa9d2d95"; // site that doesn’t send Access-Control-*
            //     try {
            //         return await axios.get(proxyurl + url);
            //     } catch (error) {
            //         console.error(error)
            //     }
            // }
           
            // this.showCourses = async (aFunction) => {
            //     return new Promise(resolve => {
            //         resolve(aFunction);
            //     });
            // }
              
            // const defer = (callee, args) => {
            //     return new Promise(resolve => {
            //         resolve(callee(args));
            //     });
            // };
           
            // this.anArray.push(defer(this.showCourses, this.getCourses())); // returns immediately
            
            // Promise.all(this.anArray).then(results => {
            //     let lectures = results[0].data.courseSection[0].lectures;
            //     let examplePlaylist = [];
            //     lectures.forEach(function(entry) {
            //         let innerArr = [entry.lectureTitle, entry.lectureLink];
            //         return examplePlaylist.push(innerArr);
            //     });
            //     console.log(examplePlaylist);
            //     loadFirst = function() {
            //         console.log(examplePlaylist[0][1]);
            //         videoEl.src = examplePlaylist[0][1];
            //         videoEl.load();
            //         $("#videoTitle").empty().append(examplePlaylist[0][0]);
            //     };
            //     loadFirst();
            //     loadPrevious = function() {
            //         let lastVideo = examplePlaylist.length - 1;
            //         $("#previousBtn").on('click', function(){
            //             progressBar.value = 0;
            //             if (vidNum === 0) {
            //                 vidNum = lastVideo;
            //                 videoEl.src = examplePlaylist[lastVideo][1];
            //                 videoEl.load();
            //                 $("#videoTitle").empty().append(examplePlaylist[lastVideo][0]);
            //             } else {
            //                 vidNum--;
            //                 videoEl.src = examplePlaylist[vidNum][1];
            //                 videoEl.load();
            //                 $("#videoTitle").empty().append(examplePlaylist[vidNum][0]);
            //             }
            //         });
            //     };
    
            //     // Load next video
            //     loadNext = function() {
            //         let lastVideo = examplePlaylist.length - 1;
            //         $("#nextBtn").on('click', function(){
            //             progressBar.value = 0;
            //             if (vidNum === lastVideo) {
            //                 vidNum = 0;
            //                 videoEl.src = examplePlaylist[0][1];
            //                 videoEl.load();
            //                 $("#videoTitle").empty().append(examplePlaylist[0][0]);
            //             } else {
            //                 vidNum++;
            //                 videoEl.src = examplePlaylist[vidNum][1];
            //                 videoEl.load();
            //                 $("#videoTitle").empty().append(examplePlaylist[vidNum][0]);
            //             }
            //         });
            //     };
            //     loadPrevious();
            //     loadNext();
            //     // console.log(results[0].data.courseSection[0].lectures[0].lectureLink);
            // });

            
            // Resets video current time to 0
            this.replay = function() {
                $("#replayBtn").on('click', function(){
                    videoEl.currentTime = 0; // throws error but still works
                });
            }

            // Toggles play/pause with button and by clicking on the video
            this.playPause = function() {
                $("#playPauseBtn, video").on('click', function(){
                    if (!this.nowPlaying) {
                        this.nowPlaying = true;
                        videoEl.play();
                    }
                    else if (this.nowPlaying) {
                        this.nowPlaying = false;
                        videoEl.pause();
                    }
                });
            }

            // Toggles play/pause with space bar keypress 
            this.spacebarPlayPause = function() {
                // Listener for user focused element in body
                $(document).on('click','body *',function(){
                    const focused = document.activeElement.tagName;
                    // Listener for key press
                    document.onkeydown = function(spacePlayPause) {
                        spacePlayPause = spacePlayPause || window.event;
                        // If key pressed is spacebar, and user has not clicked on a form
                        // then spacebar has play/pause functionality
                        if (spacePlayPause.keyCode == 32 && !this.nowPlaying && focused != "INPUT") {
                            spacePlayPause.preventDefault();
                            this.nowPlaying = true;
                            videoEl.play();
                        } 
                        else if (spacePlayPause.keyCode == 32 && this.nowPlaying && focused != "INPUT") {
                            spacePlayPause.preventDefault();
                            this.nowPlaying = false;
                            videoEl.pause();
                        } 
                        // If user has clicked on a form
                        // then spacebar has default functionality
                        else if (focused == "INPUT") {
                            return true;
                        }
                    }
                });
            };
            
            // Progress bar displays percentage of video played 
            function updateProgress() {
                let percentage = (100 / videoEl.duration) * videoEl.currentTime;
                progressBar.value = percentage.toFixed(2);
                // Text for browsers that don't support progress tag
                progressBar.innerHTML = percentage + '% played';
            }

            // Update the progress bar whenever current time is changed
            videoEl.addEventListener('timeupdate', updateProgress, false);

            // Clicking on different areas of the progress bar changes the videos current time
            this.seekBar = function() {
                function seek(evt) {
                    let percent = evt.offsetX / this.offsetWidth;
                    videoEl.currentTime = percent * videoEl.duration;
                    evt.val = Math.floor(percent / 100);
                }
                progressBar.addEventListener('click', seek); // jQuery didn't work for this...
            }
            
            // Button rewind by 10 seconds 
            this.seekMinus = function() {
                $("#seekMinusBtn").on('click', function(){
                    videoEl.currentTime = Math.min(videoEl.currentTime - skipTime, videoEl.duration);
                })
            }

            // Button fast-forward by 10 seconds
            this.seekPlus = function() {
                $("#seekPlusBtn").on('click', function(){
                    videoEl.currentTime = Math.max(videoEl.currentTime + skipTime, 0);
                })
            }

            // Update volume when range pointer is moved
            volumeBar.addEventListener('change', function(evt) {
                videoEl.volume = evt.target.value;
            });

            // Toggles mute and unmute
            this.muteVolume = function() {
                $("#muteBtn").on('click', function(){
                    if (videoEl.muted) {
                        videoEl.muted = false;
                    } else {
                        videoEl.muted = true;
                    }
                });
            }

            // Toggle fullscreen on button click with browser support
            this.toggleFullscreen = function() {
                $("#fullScreenBtn").on('click', function(){
                    if (videoEl.requestFullscreen)
                        if (document.fullScreenElement) {
                        document.cancelFullScreen();
                        } else {
                            videoEl.requestFullscreen();
                        } 
                    else if (videoEl.msRequestFullscreen)
                        if (document.msFullscreenElement) {
                            document.msExitFullscreen();
                        } else {
                            videoEl.msRequestFullscreen();
                        }
                    else if (videoEl.mozRequestFullScreen)
                        if (document.mozFullScreenElement) {
                            document.mozCancelFullScreen();
                        } else {
                            videoEl.mozRequestFullScreen();
                        }
                    else if (videoEl.webkitRequestFullscreen)
                        if (document.webkitFullscreenElement) {
                            document.webkitCancelFullScreen();
                        } else {
                            videoEl.webkitRequestFullscreen();
                        }
                    else {
                        alert("Fullscreen API is not supported");
                        
                    }
                });
            }
        };
        
        vid = new Video(true);

        vid.replay();
        vid.playPause();
        vid.spacebarPlayPause();
        vid.seekBar();
        vid.seekMinus();
        vid.seekPlus();
        vid.muteVolume();
        vid.toggleFullscreen();
        
    });

};

module.exports = vidControls;