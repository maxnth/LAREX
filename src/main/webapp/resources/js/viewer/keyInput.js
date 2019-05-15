function KeyInput(_navigationController, _controller, _gui, _selector) {
	this.isActive = true;
	const _this = this;

	document.onkeydown = function (event) {
		if (_this.isActive) {
			let validKey = false;
			const mode = _controller.getMode();

			if(mode == Mode.TEXT && _gui.isTextLineContentActive()){
				switch(event.keyCode){
					case 27: // ESC
						_controller.escape();
						_controller.endCreateReadingOrder();
						validKey = true;
						break;
					case 13: // Enter
						_controller.saveLine();
						validKey = true;
						break;
					case 83: // S
						if (event.ctrlKey) {
							_controller.exportPageXML();
							validKey = true;
						}
						break;
					case 89: // Y
						if (event.ctrlKey) {
							_controller.redo();
							validKey = true;
						}
						break;
					case 90: // Z
						if (event.ctrlKey) {
							if (event.shiftKey) {
								_controller.redo();
							} else {
								_controller.undo();
							}
							validKey = true;
						}
						break;
				}

			}else{
				switch (event.keyCode) {
					case 37: // left
						_navigationController.move(-10, 0);
						validKey = true;
						break;
					case 38: // up
						_navigationController.move(0, -10);
						validKey = true;
						break;
					case 39: // right
						_navigationController.move(10, 0);
						validKey = true;
						break;
					case 40: // left
						_navigationController.move(0, 10);
						validKey = true;
						break;
					case 32: // space
						if (!event.ctrlKey) {
							_navigationController.zoomFit();
							validKey = true;
						} else {
							if(mode === Mode.SEGMENT){
								_controller.requestSegmentation()
								validKey = true;
							}
						}
						break;
					case 187: // +
						_navigationController.zoomIn(0.1);
						validKey = true;
						break;
					case 189: // -
						_navigationController.zoomOut(0.1);
						validKey = true;
						break;
					case 17: // CTRL
						_selector.selectMultiple = true;
						validKey = true;
						break;
					case 16: // Shift
						_selector.selectBox = true;
						validKey = true;
						break;
					case 89: // Y
						if (event.ctrlKey) {
							_controller.redo();
							validKey = true;
						}
						break;
					case 90: // Z
						if (event.ctrlKey) {
							if (event.shiftKey) {
								_controller.redo();
							} else {
								_controller.undo();
							}
							validKey = true;
						}
						break;
					case 46: // DELETE
						_controller.deleteSelected();
						validKey = true;
						break;

					case 27: // ESC
						_controller.escape();
						_controller.endCreateReadingOrder();
						validKey = true;
						break;
					case 49: // 1
						if(mode === Mode.SEGMENT){
							_controller.createRectangle(ElementType.REGION);
							validKey = true;
						}
						break;
					case 50: // 2
						if(mode === Mode.SEGMENT){
							_controller.createRegionBorder();
							validKey = true;
						}
						break;
					case 51: // 3
						if(mode === Mode.SEGMENT){
							_controller.createRectangle(ElementType.SEGMENT);
							validKey = true;
						} else if(mode === Mode.LINES){
							_controller.createRectangle(ElementType.TEXTLINE);
							validKey = true;
						}
						break;
					case 52: // 4
						if(mode === Mode.SEGMENT){
							_controller.createSegmentPolygon();
							validKey = true;
						} else if(mode === Mode.LINES){
							_controller.createTextLinePolygon();
							validKey = true;
						}
						break;
					case 53: // 5
						if(mode === Mode.SEGMENT){
							_controller.createCut();
							validKey = true;
						}
						break;
					case 54: // 6
						if(mode === Mode.SEGMENT){
							_controller.displayContours();
							validKey = true;
						}
						break;
					case 67: // C
						if(mode === Mode.SEGMENT || mode === Mode.LINES){
							_controller.mergeSelectedSegments();
							validKey = true;
						}
						break;
					case 70: // F
						if(mode === Mode.SEGMENT){
							_controller.fixSelected();
							validKey = true;
						}
						break;
					case 83: // S
						if (event.ctrlKey) {
							_controller.exportPageXML();
							validKey = true;
						}
						break;
					case 18: // ALT
						//document.body.style.cursor = "move";

						_controller.applyGrid();
						break;
					default: //Debug to get key codes
					//alert(event.keyCode);
				}
			}

			if (validKey == true) {
				event.cancelBubble = true;
				event.returnValue = false;
			}
			return event.returnValue;
		}
	}

	document.onkeyup = function (event) {
		if (_this.isActive) {
			let validKey = false;

			switch (event.keyCode) {
				case 16: // Shift
					_selector.selectBox = false;
					validKey = true;
					break;
				case 17: // CTRL
					_selector.selectMultiple = false;
					validKey = true;
					break;
				case 18: // ALT
					document.body.style.cursor = "auto";
					_controller.removeGrid();
					validKey = true;
					break;
			}

			if (validKey == true) {
				event.cancelBubble = true;
				event.returnValue = false;
			}
			return event.returnValue;
		}
	}

	const wheelEvent = 'onwheel' in document ? 'wheel' : 'mousewheel DOMMouseScroll';
	$("canvas").bind(wheelEvent, function (event) {
		if (_this.isActive) {
			const canvasOffset = $(this).offset();
			let scrollDirection; //positive => down, negative => up

			switch (event.type) {
				case 'wheel': //Modern Browser is used
					scrollDirection = event.originalEvent.deltaY;
					break;
				case 'mousewheel': //Old non Firefox or Opera browser is used
					scrollDirection = event.originalEvent.wheelDelta * -1;
					break;
				case 'DOMMouseScroll': //Old version of Firefox or Opera is used
					scrollDirection = event.originalEvent.detail;
					break;
			}

			const mousepoint = new paper.Point(event.originalEvent.pageX - canvasOffset.left,
				event.originalEvent.pageY - canvasOffset.top);
			if (scrollDirection < 0) {
				_navigationController.zoomIn(0.1, mousepoint);
			} else {
				_navigationController.zoomOut(0.1, mousepoint);
			}
		}
	});
}
