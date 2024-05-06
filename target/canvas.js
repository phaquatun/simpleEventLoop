/*
 *** 	canvas : success
 ***	font finger : failed
 ***	client rect : success 
 ***	audio context : success
 ***	web GL : success
 */

//arrProfiles
var arrProfiles = [];
(() => {
    const profile = '100003199755864'; //89f3a890-a352-4624-8035-22e706a30702

    function handleProfiles() {
        for (let i = 0, length1 = profile.length; i < length1; i++) {
            let charProfile = profile[i];
            arrProfiles.push(charIsNum(charProfile));
        }
        if (arrProfiles.length < 36) {
            for (var k = 0, length3 = arrProfiles.length; k < length3; k++) {
                let charProfile = arrProfiles[k];
                arrProfiles.push(arrProfiles[k] + arrProfiles[length3 - 1 - k]);
                if (arrProfiles.length >= 36) {
                    break;
                }
            }
        }
    }

    function charIsNum(c) {
        c = c >= '0' && c <= '9' ? c + `${c}`.charCodeAt(0) : `${c}`.charCodeAt(0);
        return parseInt(c);
    }

    handleProfiles();
    //console.log(`check arrProfile ${arrProfiles}`);

})();

/*
 *** camvas
 */
(() => {

    //console.log(`check arrProfile in canvas ${arrProfiles}`);

    const getImageData = CanvasRenderingContext2D.prototype.getImageData;

    const noisify = function(canvas, context) {
        if (context) {
            const shift = {
                'r': parseFloat(`0.${arrProfiles[0]}`) * 10 - 5,
                'g': parseFloat(`0.${arrProfiles[1]}`) * 10 - 5,
                'b': parseFloat(`0.${arrProfiles[2]}`) * 10 - 5,
                'a': parseFloat(`0.${arrProfiles[3]}`) * 10 - 5,
            };
            //
            const width = canvas.width;
            const height = canvas.height;
            //
            if (width && height) {
                const imageData = getImageData.apply(context, [0, 0, width, height]);
                //
                for (let i = 0; i < height; i++) {
                    for (let j = 0; j < width; j++) {
                        const n = ((i * (width * 4)) + (j * 4));
                        imageData.data[n + 0] = imageData.data[n + 0] + shift.r;
                        imageData.data[n + 1] = imageData.data[n + 1] + shift.g;
                        imageData.data[n + 2] = imageData.data[n + 2] + shift.b;
                        imageData.data[n + 3] = imageData.data[n + 3] + shift.a;
                    }
                }
                //
                context.putImageData(imageData, 0, 0);
            }
        }
    };

    /*
     *** canvas
     */

    HTMLCanvasElement.prototype.toBlob = new Proxy(HTMLCanvasElement.prototype.toBlob, {
        apply(target, self, args) {
            //old ver noisify(self, self.getContext("2d"));
            noisify(self, self.getContext("2d"));
            //
            return Reflect.apply(target, self, args);
        }
    });
    //
    HTMLCanvasElement.prototype.toDataURL = new Proxy(HTMLCanvasElement.prototype.toDataURL, {
        apply(target, self, args) {
            //old ver noisify	noisify(self, self.getContext("2d"));
            noisify(self, self.getContext("2d"));
            //
            return Reflect.apply(target, self, args);
        }
    });
    //
    CanvasRenderingContext2D.prototype.getImageData = new Proxy(CanvasRenderingContext2D.prototype.getImageData, {
        apply(target, self, args) {
            // note old ver noisify(self.canvas, self)
            //
            noisify(self.canvas, self.self.getContext("2d"));
            return Reflect.apply(target, self, args);
        }
    });


})();


/*
 ***  font finger
 */
(() => {

    let countRoom = 0;
    let countLoop = 0;

    const rand = {
        "noise": function() {
            countLoop = countLoop >= arrProfiles.length ? countLoop % (arrProfiles.length) : countLoop;
            countRoom = countRoom >= arrProfiles.length ? 0 : countRoom;
            ++countLoop;
            ++countRoom;

            let SIGN = (arrProfiles[countRoom] + countLoop) % 2 == 0 ? -1 : 1;
            return Math.floor(parseFloat(`0.${arrProfiles[countRoom]}`) * parseFloat(`0.${countLoop}`) +
                SIGN * (parseFloat(`0.${arrProfiles[countRoom]}`))
            );
        },
        "sign": function() {
            countRoom = countRoom >= arrProfiles.length ? 0 : countRoom;
            ++countRoom;
            const tmp = [-1, -1, -1, -1, -1, -1, +1, -1, -1, -1];
            const index = Math.floor(parseFloat(`0.${arrProfiles[countRoom]}`) * tmp.length);
            return tmp[index];
        }
    };
    //
    Object.defineProperty(HTMLElement.prototype, "offsetHeight", {
        "get": new Proxy(Object.getOwnPropertyDescriptor(HTMLElement.prototype, "offsetHeight").get, {
            apply(target, self, args) {
                try {
                    const height = Math.floor(self.getBoundingClientRect().height);
                    const valid = height && rand.sign() === 1;
                    const result = valid ? height + rand.noise() : height;
                    //
                    if (valid && result !== height) {

                    }
                    //
                    return result;
                } catch (e) {
                    //return Reflect.apply(target, self, args);
                }
            }
        })
    });
    //
    Object.defineProperty(HTMLElement.prototype, "offsetWidth", {
        "get": new Proxy(Object.getOwnPropertyDescriptor(HTMLElement.prototype, "offsetWidth").get, {
            apply(target, self, args) {
                const width = Math.floor(self.getBoundingClientRect().width);
                const valid = width && rand.sign() === 1;
                const result = valid ? width + rand.noise() : width;
                //
                if (valid && result !== width) {

                }
                //
                return result;
            }
        })
    });

})();

/*
 ***  client rect
 */

(() => {

    let countRoom = 0;
    let countLoop = 0;
    let config = {
        "noise": {
            "DOMRect": 0.00000001,
            "DOMRectReadOnly": 0.000001
        },
        "metrics": {
            "DOMRect": ['x', 'y', "width", "height"],
            "DOMRectReadOnly": ["top", "right", "bottom", "left"]
        },
        "method": {
            "DOMRect": function(e) {
                try {
                    Object.defineProperty(DOMRect.prototype, e, {
                        "get": new Proxy(Object.getOwnPropertyDescriptor(DOMRect.prototype, e).get, {
                            apply(target, self, args) {
                                const result = Reflect.apply(target, self, args);
                                /*
                                old ver
                                const _result = result * (1 + (Math.random() < 0.5 ? -1 : +1) * config.noise.DOMRect);
                                //console.log(`_result DOMRect ${_result}`);
                                
                                */
                                if (countRoom >= arrProfiles.length) {
                                    ++countLoop;
                                }
                                if (countLoop >= 7) {
                                    countLoop = 0;
                                }

                                countRoom = countRoom >= arrProfiles.length ? 0 : ++countRoom;
                                let room = parseFloat(`0.${arrProfiles[countRoom]}`);

                                let _result = result * (1 + (room < 0.5 ? -1 : +1) * config.noise.DOMRect);
                                _result = _result + parseFloat(`0.${arrProfiles[countRoom]}`);
                                //console.log(`check value room client rect DOMRect ${_result}`);
                                //console.log(`check parseIn  DOMRect ${parseFloat(`0.${arrProfiles[countRoom]}`) }`)

                                return _result;
                            }
                        })
                    });
                } catch (e) {
                    //console.error(e);
                }
            },
            "DOMRectReadOnly": function(e) {
                try {
                    Object.defineProperty(DOMRectReadOnly.prototype, e, {
                        "get": new Proxy(Object.getOwnPropertyDescriptor(DOMRectReadOnly.prototype, e).get, {
                            apply(target, self, args) {
                                const result = Reflect.apply(target, self, args);
                                /*
                                old ver
                                const _result = result * (1 + (Math.random() < 0.5 ? -1 : +1) * config.noise.DOMRectReadOnly);
                                //console.log(`_result DOMRectReadOnly ${_result}`);
								*/
                                if (countRoom >= arrProfiles.length) {
                                    ++countLoop;
                                }
                                if (countLoop >= 7) {
                                    countLoop = 0;
                                }

                                countRoom = countRoom >= arrProfiles.length ? 0 : ++countRoom;
                                let room = parseFloat(`0.${arrProfiles[countRoom]}`);

                                let _result = result * (1 + (room < 0.5 ? -1 : +1) * config.noise.DOMRectReadOnly);
                                _result = _result + parseFloat(`0.${arrProfiles[countRoom]}`);
                                //console.log(`_result DOMRectReadOnly ${_result}`);
                                //console.log(`check parseIn  DOMRectReadOnly ${parseFloat(`0.${arrProfiles[countRoom]}`) }`);

                                return _result;
                            }
                        })
                    });
                } catch (e) {
                    //console.error(e);
                }
            }
        }
    };
    //
    config.method.DOMRect(config.metrics.DOMRect.sort(() => 0.5 - arrProfiles[0])[0]);
    config.method.DOMRectReadOnly(config.metrics.DOMRectReadOnly.sort(() => 0.5 - arrProfiles[1])[0]);
})();



/*
 *** audio
 */
(() => {

    let max = Math.max(...arrProfiles);
    let countRoom = 0;
    let countLoop = 1;

    function roomIndex(valueLoop) {
        let i = valueLoop;
        valueLoop = valueLoop / 100;
        let size = arrProfiles.length;

        if (valueLoop < size) {
            let index = parseFloat(`0.${arrProfiles[valueLoop]}`) *(arrProfiles[valueLoop] / max * i);
            return Math.floor(index);
        }

        if (valueLoop > size) {
            valueLoop = valueLoop % size;
            let index =  parseFloat(`0.${arrProfiles[valueLoop]}`) *( arrProfiles[valueLoop] / max * i);
            return Math.floor(index);
        }
    }
    const context = {
        "BUFFER": null,
        "getChannelData": function(e) {
            e.prototype.getChannelData = new Proxy(e.prototype.getChannelData, {
                apply(target, self, args) {
                    const results_1 = Reflect.apply(target, self, args);
                    //
                    if (context.BUFFER !== results_1) {
                        context.BUFFER = results_1;
                        //
                        for (let i = 0; i < results_1.length; i += 100) {
                            let index = roomIndex(i);
                            results_1[index] = results_1[index] + roomIndex(i) * 0.0000001;
                        }
                    }
                    //
                    // //console.log('val results_1 ' +results_1);
                    return results_1;
                }
            });
        },
        "createAnalyser": function(e) {
            e.prototype.__proto__.createAnalyser = new Proxy(e.prototype.__proto__.createAnalyser, {
                apply(target, self, args) {
                    const results_2 = Reflect.apply(target, self, args);
                    //
                    results_2.__proto__.getFloatFrequencyData = new Proxy(results_2.__proto__.getFloatFrequencyData, {
                        apply(target, self, args) {
                            const results_3 = Reflect.apply(target, self, args);
                            //
                            for (let i = 0; i < arguments[0].length; i += 100) {
                                let index = roomIndex(i);
                                arguments[0][index] = arguments[0][index] + roomIndex(i) * 0.1;
                            }
                            //
                            return results_3;
                        }
                    });
                    //
                    return results_2;
                }
            });
        }
    };
    //
    context.getChannelData(AudioBuffer);
    context.createAnalyser(AudioContext);
    context.createAnalyser(OfflineAudioContext);
})();

/*
 *** web GL
 */
(() => {
    //return  parseInt(`0.${Math.max(...arrProfiles)}`);
    let countRoom = 0;
    let countLoop = 0;

    let config = {
        "random": {
            "value": function() {
                countRoom = countRoom >= arrProfiles.length ? 0 : countRoom;
                ++countRoom;
                countLoop = countLoop >= arrProfiles.length ? 0 : countLoop;
                ++countLoop;

                let valueWebGl = parseFloat(`0.${arrProfiles[countRoom]}`) * parseFloat(`0.${arrProfiles[countLoop]}`);
                return valueWebGl;
            },
            "item": function(e) {
                let rand = e.length * config.random.value();
                return e[Math.floor(rand)];
            },
            "number": function(power) {
                let tmp = [];
                for (let i = 0; i < power.length; i++) {
                    tmp.push(Math.pow(2, power[i]));
                }
                /*  */
                return config.random.item(tmp);
            },
            "int": function(power) {
                let tmp = [];
                for (let i = 0; i < power.length; i++) {
                    let n = Math.pow(2, power[i]);
                    tmp.push(new Int32Array([n, n]));
                }
                /*  */
                return config.random.item(tmp);
            },
            "float": function(power) {
                let tmp = [];
                for (let i = 0; i < power.length; i++) {
                    let n = Math.pow(2, power[i]);
                    tmp.push(new Float32Array([1, n]));
                }
                /*  */
                return config.random.item(tmp);
            }
        },
        "spoof": {
            "webgl": {
                "buffer": function(target) {
                    let proto = target.prototype ? target.prototype : target.__proto__;
                    //
                    proto.bufferData = new Proxy(proto.bufferData, {
                        apply(target, self, args) {
                            let index = Math.floor(config.random.value() * args[1].length);
                            let noise = args[1][index] !== undefined ? 0.1 * config.random.value() * args[1][index] : 0;
                            //
                            args[1][index] = args[1][index] + noise;
                            //
                            return Reflect.apply(target, self, args);
                        }
                    });
                },
                "parameter": function(target) {
                    let proto = target.prototype ? target.prototype : target.__proto__;
                    //
                    proto.getParameter = new Proxy(proto.getParameter, {
                        apply(target, self, args) {
                            window.top.postMessage("webgl-defender-alert", '*');
                            //
                            if (args[0] === 3415) return 0;
                            else if (args[0] === 3414) return 24;
                            else if (args[0] === 36348) return 30;
                            else if (args[0] === 7936) return "WebKit";
                            else if (args[0] === 37445) return "Google Inc.";
                            else if (args[0] === 7937) return "WebKit WebGL";
                            else if (args[0] === 3379) return config.random.number([14, 15]);
                            else if (args[0] === 36347) return config.random.number([12, 13]);
                            else if (args[0] === 34076) return config.random.number([14, 15]);
                            else if (args[0] === 34024) return config.random.number([14, 15]);
                            else if (args[0] === 3386) return config.random.int([13, 14, 15]);
                            else if (args[0] === 3413) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 3412) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 3411) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 3410) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 34047) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 34930) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 34921) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 35660) return config.random.number([1, 2, 3, 4]);
                            else if (args[0] === 35661) return config.random.number([4, 5, 6, 7, 8]);
                            else if (args[0] === 36349) return config.random.number([10, 11, 12, 13]);
                            else if (args[0] === 33902) return config.random.float([0, 10, 11, 12, 13]);
                            else if (args[0] === 33901) return config.random.float([0, 10, 11, 12, 13]);
                            else if (args[0] === 37446) return config.random.item(["Graphics", "HD Graphics", "Intel(R) HD Graphics"]);
                            else if (args[0] === 7938) return config.random.item(["WebGL 1.0", "WebGL 1.0 (OpenGL)", "WebGL 1.0 (OpenGL Chromium)"]);
                            else if (args[0] === 35724) return config.random.item(["WebGL", "WebGL GLSL", "WebGL GLSL ES", "WebGL GLSL ES (OpenGL Chromium"]);
                            //
                            return Reflect.apply(target, self, args);
                        }
                    });
                }
            }
        }
    };

    config.spoof.webgl.buffer(WebGLRenderingContext);
    config.spoof.webgl.buffer(WebGL2RenderingContext);
    config.spoof.webgl.parameter(WebGLRenderingContext);
    config.spoof.webgl.parameter(WebGL2RenderingContext);

})();